package nl.limesco.cserv.invoicing;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceConstructor;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.pricing.api.DataPricing;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.pricing.api.SmsPricing;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricing;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.sim.api.MonthedInvoice;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class InvoiceConstructorImpl implements InvoiceConstructor {
	
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	// TODO: Don't use hardcoded prices (issue #42)
	private static final long ACTIVATION_PRICE = 347107;
	
	private static final long CONTRIBUTION_PRICE = 41322;
	
	private volatile InvoiceService invoiceService;

	private volatile PricingService pricingService;
	
	private volatile CdrService cdrService;
	
	private volatile SimService simService;

	@Override
	public Invoice constructInvoiceForAccount(Calendar day, String accountId, boolean dry_run) throws IdAllocationException {
		// Prevent multiple invoice generations from happening simultaneously
		cdrService.lock();
		invoiceService.lock();
		try {
			return lockedConstructInvoiceForAccount(day, accountId, dry_run);
		} finally {
			invoiceService.unlock();
			cdrService.unlock();
		}
	}
	
	private Invoice lockedConstructInvoiceForAccount(Calendar day, String accountId, boolean dry_run) throws IdAllocationException {
		
		final UUID builderUUID = UUID.randomUUID();

		/* XXX: This is not right, must be fixed by issue #37 .. with the current situation the result is correct */
		final Collection<? extends Sim> sims = simService.getActiveSimsByOwnerAccountId(accountId);
		if (sims.isEmpty()) {
			return null;
		}

		if (sims.size() > 1) {
			return null; // XXX: for now, until #67 is fixed
		}
		final Sim accountSim = sims.iterator().next();
		
		// Until when will we be processing monthly costs?
		// (normally "day" is today, so endOfSubscriptionPeriod will be the end of this month)
		final Calendar endOfSubscriptionPeriod = (Calendar) day.clone();
		endOfSubscriptionPeriod.set(Calendar.DAY_OF_MONTH, 1);
		endOfSubscriptionPeriod.add(Calendar.MONTH, 1);
		endOfSubscriptionPeriod.add(Calendar.DAY_OF_MONTH, -1);
		
		// all activated SIMs which did not have an activation invoice yet
		final Collection<? extends Sim> simActivations = simService.getActivatedSimsWithoutActivationInvoiceByOwnerAccountId(accountId);
		// all activated SIMs which were not invoiced yet on or after this day
		final Collection<? extends Sim> subscriptionFees = simService.getActivatedSimsLastInvoicedBeforeByOwnerAccountId(day, accountId);
		// all CDR's which were not invoiced yet (XXX #67) (XXX only until given day)
		final Collection<? extends Cdr> cdrs = cdrService.getUninvoicedCdrsForAccount(accountId, builderUUID.toString());
		
		// Start building the invoice
		final InvoiceBuilder builder = invoiceService.buildInvoice()
				.accountId(accountId)
				.creationDate(day)
				.currency(InvoiceCurrency.EUR);
		
		// Include the activations
		if (!simActivations.isEmpty()) {
			builder.normalItemLine("Activatie SIM-kaart", simActivations.size(), ACTIVATION_PRICE, 0.21);
		}
		
		// Include the subscription fees for all SIMs and for all past months
		int numberOfMonthForCostContribution = 0;
		final Map<SubscriptionKey, Integer> subscriptions = Maps.newHashMap();
		for (Sim sim : subscriptionFees) {
			final Optional<Calendar> contractStartDate = sim.getContractStartDate();
			if (!contractStartDate.isPresent()) {
				continue;
			}
			
			final Optional<MonthedInvoice> lastMonthlyFeesInvoice = sim.getLastMonthlyFeesInvoice();
			// itemStart: the next month of monthly fees invoice, i.e.
			// the month after the last monthly fees invoice, otherwise date of start of contract
			final Calendar itemStart;
			if (lastMonthlyFeesInvoice.isPresent()) {
				final Calendar monthStart = Calendar.getInstance();
				monthStart.setTimeZone(TimeZone.getTimeZone("UTC"));
				monthStart.setTimeInMillis(0);
				monthStart.set(Calendar.YEAR, lastMonthlyFeesInvoice.get().getYear());
				monthStart.set(Calendar.MONTH, lastMonthlyFeesInvoice.get().getMonth());
				monthStart.add(Calendar.MONTH, 1);
				itemStart = monthStart;
			} else {
				// First day of the month after the contractStartDate
				itemStart = (Calendar)contractStartDate.get().clone();
				itemStart.setTimeZone(TimeZone.getTimeZone("UTC"));
				itemStart.add(Calendar.MONTH, 1);
				itemStart.set(Calendar.DAY_OF_MONTH, 1);
			}
			
			// compute subscription costs starting with itemStart, ending in end of that month;
			// repeat this until we go past the endOfSubscriptionPeriod (end of this month)
			// this will accumulate subscription costs for all past months
			Calendar start = (Calendar) itemStart.clone();
			while (true) {
				final Calendar end = (Calendar) start.clone();
				end.set(Calendar.DAY_OF_MONTH, 1);
				end.add(Calendar.MONTH, 1);
				end.add(Calendar.DAY_OF_MONTH, -1);
				
				final int days = 1 + (int) ((end.getTimeInMillis() - start.getTimeInMillis()) / (24 * 60 * 60 * 1000));
				final SubscriptionKey key = new SubscriptionKey(start, days, sim.getApnType());
				
				if (subscriptions.containsKey(key)) {
					subscriptions.put(key, Integer.valueOf(subscriptions.get(key).intValue() + 1));
				} else {
					subscriptions.put(key, Integer.valueOf(1));
				}
				
				if (!sim.isExemptFromCostContribution()) {
					numberOfMonthForCostContribution++;
				}
				
				if (!end.before(endOfSubscriptionPeriod)) {
					break;
				} else {
					start = (Calendar) end.clone();
					start.add(Calendar.DAY_OF_MONTH, 1);
					assert(start.get(Calendar.DAY_OF_MONTH) == 1);
				}
			}
		}
		
		// Process item lines for subscription fees for all SIMs and all past months
		for (Entry<SubscriptionKey, Integer> subscription : subscriptions.entrySet()) {
			final String formattedStart = DAY_FORMAT.format(subscription.getKey().getStart().getTime());
			final Calendar end = (Calendar) subscription.getKey().getStart();
			end.add(Calendar.DATE, subscription.getKey().getDays() - 1);
			final String formattedEnd = DAY_FORMAT.format(end.getTime());
			final String formattedApnType = subscription.getKey().getApnType().getFriendlyName();
			final long monthlyPrice = subscription.getKey().getApnType().getMonthlyPrice();
			final long itemPrice = computePartialMonthPrice(end.get(Calendar.DAY_OF_MONTH), subscription.getKey().getDays(), monthlyPrice);
			final String description = String.format("Vaste kosten %s - %s (%s)", formattedStart, formattedEnd, formattedApnType);
			final String multiLine1 = String.format("Vaste kosten (%s)", formattedApnType);
			final String multiLine2 = String.format("%s - %s", formattedStart, formattedEnd);
			builder.normalItemLine(description, Arrays.asList(multiLine1, multiLine2), subscription.getValue().intValue(), itemPrice, 0.21);
		}
		
		// Split CDRs into types
		final Set<VoiceCdr> voiceCdrs = Sets.newHashSet();
		final Set<SmsCdr> smsCdrs = Sets.newHashSet();
		final Set<DataCdr> dataCdrs = Sets.newHashSet();
		for (Cdr cdr : cdrs) {
			if (cdr instanceof VoiceCdr) {
				voiceCdrs.add((VoiceCdr) cdr);
			} else if (cdr instanceof SmsCdr) {
				smsCdrs.add((SmsCdr) cdr);
			} else if (cdr instanceof DataCdr) {
				dataCdrs.add((DataCdr) cdr);
			}
		}
		
		// Include the voice CDRs
		final Map<String, CombinedDuration> durations = Maps.newHashMap();
		for (VoiceCdr cdr : voiceCdrs) {
			final Optional<Cdr.Pricing> pricing = cdr.getPricing();
			if (!pricing.isPresent() || !cdr.isConnected()) {
				continue;
			}
			
			final String pricingRuleId = pricing.get().getPricingRuleId();
			if (!durations.containsKey(pricingRuleId)) {
				durations.put(pricingRuleId, new CombinedDuration());
			}
			durations.get(pricingRuleId).addCdr(cdr);
		}
		
		for (Entry<String, CombinedDuration> duration : durations.entrySet()) {
			final Optional<? extends VoicePricingRule> pricingRule = pricingService.getPricingRuleById(VoicePricingRule.class, duration.getKey());
			if (!pricingRule.isPresent()) {
				continue;
			}
			
			final VoicePricing price = pricingRule.get().getPrice();
			if (pricingRule.get().isHidden() && price.getPerCall() == 0 && price.getPerMinute() == 0) {
				continue;
			}
			
			final CombinedDuration cd = duration.getValue();
			builder.durationItemLine("Bellen " + pricingRule.get().getDescription(), price.getPerCall(), price.getPerMinute(), cd.getCount(), cd.getSeconds(), 0.21);
		}

		// Include the SMS CDRs
		final Map<String, Integer> smsRules = Maps.newHashMap();
		for (SmsCdr cdr : smsCdrs) {
			final Optional<Cdr.Pricing> pricing = cdr.getPricing();
			if (!pricing.isPresent()) {
				continue;
			}
			
			final String pricingRuleId = pricing.get().getPricingRuleId();
			if (smsRules.containsKey(pricingRuleId)) {
				smsRules.put(pricingRuleId, Integer.valueOf(1 + smsRules.get(pricingRuleId).intValue()));
			} else {
				smsRules.put(pricingRuleId, Integer.valueOf(1));
			}
		}
		
		for (Entry<String, Integer> smsRuleEntry : smsRules.entrySet()) {
			final Optional<? extends SmsPricingRule> pricingRule = pricingService.getPricingRuleById(SmsPricingRule.class, smsRuleEntry.getKey());
			if (!pricingRule.isPresent()) {
				continue;
			}
			
			final SmsPricing price = pricingRule.get().getPrice();
			if (pricingRule.get().isHidden() && price.getPerSms() == 0) {
				continue;
			}
			
			builder.normalItemLine(pricingRule.get().getDescription(), smsRuleEntry.getValue().intValue(), price.getPerSms(), 0.21);
		}

		// Include the data CDRs
		final Map<RuleAndMonth, MonthlyBundleUsage> dataRules = Maps.newHashMap();
		for (DataCdr cdr : dataCdrs) {
			final Optional<Cdr.Pricing> pricing = cdr.getPricing();
			if (!pricing.isPresent()) {
				continue;
			}

			/* XXX: This is not right, must be fixed by issue #37 .. with the current situation the result is correct */
			final Calendar cdrTime = cdr.getTime();
			final boolean inContract;
			if (accountSim.getContractStartDate().isPresent()) {
				inContract = accountSim.getContractStartDate().get().before(cdrTime);
			} else {
				inContract = false;
			}
			final RuleAndMonth ruleAndMonth = new RuleAndMonth(cdrTime.get(Calendar.YEAR), cdrTime.get(Calendar.MONTH), pricing.get().getPricingRuleId(), inContract);
			if (!dataRules.containsKey(ruleAndMonth)) {
				dataRules.put(ruleAndMonth, new MonthlyBundleUsage(accountSim.getApnType().getBundleKilobytes()));
			}
			dataRules.get(ruleAndMonth).add(cdr.getKilobytes());
		}
		
		for (Entry<RuleAndMonth, MonthlyBundleUsage> dataRuleEntry : dataRules.entrySet()) {
			final RuleAndMonth ruleAndMonth = dataRuleEntry.getKey();
			final Optional<? extends DataPricingRule> pricingRule = pricingService.getPricingRuleById(DataPricingRule.class, ruleAndMonth.getRule());
			if (!pricingRule.isPresent()) {
				continue;
			}
			
			final DataPricing price = pricingRule.get().getPrice();
			if (pricingRule.get().isHidden() && price.getPerKilobyte() == 0) {
				continue;
			}
			
			final MonthlyBundleUsage monthlyUsage = dataRuleEntry.getValue();
			if (monthlyUsage.getBundle() > 0 && monthlyUsage.getCount() > 0 && ruleAndMonth.isInContract()) {
				final String description = String.format("%s (bundel %02d-%4d)", pricingRule.get().getDescription(), 1 + ruleAndMonth.getMonth() % 12, ruleAndMonth.getMonth() / 12);
				builder.normalItemLine(description, Math.min(monthlyUsage.getCount(), monthlyUsage.getBundle()), 0, 0.21);
			}
			if (monthlyUsage.getCount() > monthlyUsage.getBundle() || !ruleAndMonth.isInContract()) {
				final String description2 = String.format("%s (%02d-%4d)", pricingRule.get().getDescription(), 1 + ruleAndMonth.getMonth() % 12, ruleAndMonth.getMonth() / 12);
				if (ruleAndMonth.isInContract()) {
					builder.normalItemLine(description2, monthlyUsage.getCount() - monthlyUsage.getBundle(), price.getPerKilobyte(), 0.21);
				} else {
					builder.normalItemLine(description2, monthlyUsage.getCount(), price.getPerKilobyte(), 0.21);
				}
			}
		}
		
		// Include the cost contribution
		if (numberOfMonthForCostContribution > 0) {
			builder.normalItemLine("Bijdrage vaste kosten", numberOfMonthForCostContribution, CONTRIBUTION_PRICE, 0.21);
		}
		
		final Invoice invoice = builder.buildInvoice();
		if (!dry_run && invoice.getTotalWithTaxes() > 0) {
			invoiceService.storeInvoice(invoice);
		
			for (Sim sim : simActivations) {
				sim.setActivationInvoiceId(invoice.getId());
				simService.storeActivationInvoiceId(sim);
			}
			
			for (Sim sim : subscriptionFees) {
				sim.setLastMonthlyFeesInvoice(new MonthedInvoice(day.get(Calendar.YEAR), day.get(Calendar.MONTH), invoice.getId()));
				simService.storeLastMonthlyFeesInvoice(sim);
			}
			
			cdrService.setInvoiceIdForBuilder(builderUUID.toString(), invoice.getId());
		}
		
		return invoice;
	}

	private static long computePartialMonthPrice(int daysInMonth, int days, long monthlyPrice) {
		final BigDecimal daysInMonthBD = BigDecimal.valueOf(daysInMonth);
		final BigDecimal daysBD = BigDecimal.valueOf(days);
		final BigDecimal monthlyPriceBD = BigDecimal.valueOf(monthlyPrice);
		return monthlyPriceBD.multiply(daysBD).divideToIntegralValue(daysInMonthBD).longValue();
	}

}
