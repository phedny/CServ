package nl.limesco.cserv.invoicing;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.sim.api.CallConnectivityType;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import org.osgi.service.log.LogService;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

public class BatchInvoicingServiceImpl implements BatchInvoicingService {
	
	private final InvoiceConstructor invoiceConstructor;
	
	private volatile InvoiceService invoiceService;
	
	private volatile PricingService pricingService;
	
	private volatile SimService simService;
	
	private volatile CdrService cdrService;
	
	private volatile LogService logService;
	
	public BatchInvoicingServiceImpl() {
		this(new InvoiceConstructor());
	}
	
	public BatchInvoicingServiceImpl(InvoiceConstructor invoiceConstructor) {
		this.invoiceConstructor = invoiceConstructor;
	}

	public Object[] getComposition() {
		return new Object[] { this, invoiceConstructor };
	}

	@Override
	public void runBatch(Calendar day) {
		computePricingForUnpricedCdrs();
		
		final Set<String> accounts = Sets.newHashSet();
		accounts.addAll(findAccountsWithInvoicableSimActivations());
		accounts.addAll(findAccountsWithInvoicableSubscriptionFees(day));
		accounts.addAll(findAccountsWithInvoicableCdrs());
		
		logService.log(LogService.LOG_INFO, "Going to construct invoices for " + accounts.size() + " accounts");
		for (String account : accounts) {
			logService.log(LogService.LOG_INFO, "Constructing invoice for " + account);
			try {
				final Invoice invoice = invoiceConstructor.constructInvoiceForAccount(day, account);
			} catch (IdAllocationException e) {
				logService.log(LogService.LOG_WARNING, "Failed to allocate ID to invoice for " + account);
			}
		}
	}

	private void computePricingForUnpricedCdrs() {
		for (Cdr cdr : cdrService.getUnpricedCdrs()) {
			try {
				if (!cdr.getAccount().isPresent()) {
					continue;
				}
				
				/* XXX: This is not right, must be fixed by issue #37 .. with the current situation the result is correct */
				final Collection<? extends Sim> sims = simService.getSimsByOwnerAccountId(cdr.getAccount().get());
				if (sims.isEmpty()) {
					continue;
				}
				
				final Sim sim = sims.iterator().next();
				final Optional<CallConnectivityType> callConnectivityType = sim.getCallConnectivityType();
				if (!callConnectivityType.isPresent()) {
					continue;
				}
				
				if (cdr instanceof VoiceCdr) {
					final VoicePricingRule pricingRule = pricingService.getApplicablePricingRule((VoiceCdr) cdr, callConnectivityType.get());
					final long price = pricingRule.getPriceForCdr(cdr, callConnectivityType.get());
					final long cost = pricingRule.getCostForCdr(cdr, callConnectivityType.get());
					cdrService.storePricingForCdr(cdr, pricingRule.getId(), price, cost);
				} else if (cdr instanceof SmsCdr) {
					final SmsPricingRule pricingRule = pricingService.getApplicablePricingRule((SmsCdr) cdr);
					final long price = pricingRule.getPriceForCdr(cdr);
					final long cost = pricingRule.getCostForCdr(cdr);
					cdrService.storePricingForCdr(cdr, pricingRule.getId(), price, cost);
				} else if (cdr instanceof DataCdr) {
					final DataPricingRule pricingRule = pricingService.getApplicablePricingRule((DataCdr) cdr);
					final long price = pricingRule.getPriceForCdr(cdr);
					final long cost = pricingRule.getCostForCdr(cdr);
					cdrService.storePricingForCdr(cdr, pricingRule.getId(), price, cost);
				} else {
					continue;
				}
				
			} catch (NoApplicablePricingRuleException e) {
				logService.log(LogService.LOG_WARNING, "Failed to obtain pricing rule for CDR " + cdr.getSource() + "/" + cdr.getCallId());
			} catch (PricingRuleNotApplicableException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	private Collection<? extends String> findAccountsWithInvoicableSimActivations() {
		final Set<String> accountIds = Sets.newHashSet();
		for (Sim sim : simService.getActivatedSimsWithoutActivationInvoice()) {
			if(sim.getOwnerAccountId().isPresent()) {
				accountIds.add(sim.getOwnerAccountId().get());
			}
		}
		return accountIds;
	}

	private Collection<? extends String> findAccountsWithInvoicableSubscriptionFees(Calendar day) {
		final Set<String> accountIds = Sets.newHashSet();
		for (Sim sim : simService.getActivatedSimsLastInvoicedBefore(day)) {
			if(sim.getOwnerAccountId().isPresent()) {
				accountIds.add(sim.getOwnerAccountId().get());
			}
		}
		return accountIds;
	}

	private Collection<? extends String> findAccountsWithInvoicableCdrs() {
		final Set<String> accountIds = Sets.newHashSet();
		for (Cdr cdr : cdrService.getUninvoicedCdrs()) {
			if (!cdr.getAccount().isPresent()) {
				continue;
			}
			
			accountIds.add(cdr.getAccount().get());
		}
		return accountIds;
	}

}
