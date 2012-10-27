package nl.limesco.cserv.invoicing;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.pricing.api.Pricing;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.sim.api.MonthedInvoice;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class InvoiceConstructor {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	// TODO: Don't use hardcoded prices (issue #42)
	private static final long ACTIVATION_PRICE = 347107;
	
	private volatile InvoiceService invoiceService;

	private volatile AccountService accountService;
	
	private volatile PricingService pricingService;
	
	private volatile CdrService cdrService;
	
	private volatile SimService simService;

	public Invoice constructInvoiceForAccount(Calendar day, String accountId) {
		
		// Compute end of subscription period
		final Calendar endOfSubscriptionPeriod = (Calendar) day.clone();
		endOfSubscriptionPeriod.set(Calendar.DAY_OF_MONTH, 1);
		endOfSubscriptionPeriod.add(Calendar.MONTH, 1);
		endOfSubscriptionPeriod.add(Calendar.DAY_OF_MONTH, -1);
		
		// Collect everything we need
		final Collection<? extends Sim> simActivations = getInvoicableSimActivations(accountId);
		final Collection<? extends Sim> subscriptionFees = getInvoicableSubscriptionFees(day, accountId);
		final Collection<? extends Cdr> cdrs = getInvoicableCdrs(accountId);
		
		// Start building the invoice
		final InvoiceBuilder builder = invoiceService.buildInvoice()
				.accountId(accountId)
				.creationDate(day)
				.currency(InvoiceCurrency.EUR);
		
		// Include the activations
		if (!simActivations.isEmpty()) {
			builder.normalItemLine("Activatie SIM-kaart", simActivations.size(), ACTIVATION_PRICE, 0.21);
		}
		
		// Include the subscription fees
		final Map<SubscriptionKey, Integer> subscriptions = Maps.newHashMap();
		for (Sim sim : subscriptionFees) {
			final Optional<Calendar> contractStartDate = sim.getContractStartDate();
			if (!contractStartDate.isPresent()) {
				continue;
			}
			
			final Optional<MonthedInvoice> lastMonthlyFeesInvoice = sim.getLastMonthlyFeesInvoice();
			final SubscriptionKey key;
			if (lastMonthlyFeesInvoice.isPresent()) {
				final Calendar monthStart = Calendar.getInstance();
				monthStart.setTimeZone(TimeZone.getTimeZone("UTC"));
				monthStart.setTimeInMillis(0);
				monthStart.set(Calendar.YEAR, lastMonthlyFeesInvoice.get().year);
				monthStart.set(Calendar.MONTH, lastMonthlyFeesInvoice.get().month);
				final int days = 1 + (int) ((endOfSubscriptionPeriod.getTimeInMillis() - monthStart.getTimeInMillis()) / (24 * 60 * 60 * 1000));
				key = new SubscriptionKey(monthStart, days, sim.getApnType());
			} else {
				final int days = 1 + (int) ((endOfSubscriptionPeriod.getTimeInMillis() - contractStartDate.get().getTimeInMillis()) / (24 * 60 * 60 * 1000));
				key = new SubscriptionKey(contractStartDate.get(), days, sim.getApnType());
			}
			if (subscriptions.containsKey(key)) {
				subscriptions.put(key, Integer.valueOf(subscriptions.get(key).intValue() + 1));
			} else {
				subscriptions.put(key, Integer.valueOf(1));
			}
		}
		
		for (Entry<SubscriptionKey, Integer> subscription : subscriptions.entrySet()) {
			final String formattedStart = DAY_FORMAT.format(subscription.getKey().getStart().getTime());
			final String formattedEnd = DAY_FORMAT.format(endOfSubscriptionPeriod.getTime());
			final String formattedApnType = subscription.getKey().getApnType().getFriendlyName();
			final long monthlyPrice = subscription.getKey().getApnType().getMonthlyPrice();
			final long itemPrice = computePartialMonthPrice(endOfSubscriptionPeriod.get(Calendar.DAY_OF_MONTH), subscription.getKey().getDays(), monthlyPrice);
			final String description = String.format("Vaste kosten %s - %s (%s)", formattedStart, formattedEnd, formattedApnType);
			builder.normalItemLine(description, subscription.getValue().intValue(), itemPrice, 0.21);
		}
		
		// Include the CDRs
		final Map<String, CombinedDuration> durations = Maps.newHashMap();
		for (Cdr cdr : cdrs) {
			final Optional<Cdr.Pricing> pricing = cdr.getPricing();
			if (!pricing.isPresent()) {
				continue;
			}
			
			final String pricingRuleId = pricing.get().getPricingRuleId();
			if (!durations.containsKey(pricingRuleId)) {
				durations.put(pricingRuleId, new CombinedDuration());
			}
			durations.get(pricingRuleId).addCdr(cdr);
		}
		
		for (Entry<String, CombinedDuration> duration : durations.entrySet()) {
			final Optional<? extends PricingRule> pricingRule = pricingService.getPricingRuleById(duration.getKey());
			if (!pricingRule.isPresent()) {
				continue;
			}
			
			final Pricing price = pricingRule.get().getPrice();
			final CombinedDuration cd = duration.getValue();
			builder.durationItemLine("Bellen " + pricingRule.get().getId(), price.getPerCall(), price.getPerMinute(), cd.getCount(), cd.getSeconds(), 0.21);
		}
		
		return builder.build();
	}

	private static long computePartialMonthPrice(int daysInMonth, int days, long monthlyPrice) {
		final BigDecimal daysInMonthBD = BigDecimal.valueOf(daysInMonth);
		final BigDecimal daysBD = BigDecimal.valueOf(days);
		final BigDecimal monthlyPriceBD = BigDecimal.valueOf(monthlyPrice);
		return monthlyPriceBD.multiply(daysBD).divideToIntegralValue(daysInMonthBD).longValue();
	}

	private Collection<? extends Sim> getInvoicableSimActivations(String accountId) {
		return simService.getActivatedSimsWithoutActivationInvoiceByOwnerAccountId(accountId);
	}

	private Collection<? extends Sim> getInvoicableSubscriptionFees(Calendar day, String accountId) {
		return simService.getActivatedSimsLastInvoicedBeforeByOwnerAccountId(day, accountId);
	}

	private Collection<? extends Cdr> getInvoicableCdrs(String accountId) {
		return cdrService.getUninvoicedCdrsForAccount(accountId);
	}
	
}
