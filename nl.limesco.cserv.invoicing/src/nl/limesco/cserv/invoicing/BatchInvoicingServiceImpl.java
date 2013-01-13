package nl.limesco.cserv.invoicing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.lib.email.api.EmailService;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.sim.api.CallConnectivityType;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.osgi.service.log.LogService;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class BatchInvoicingServiceImpl implements BatchInvoicingService {
	
	private final InvoiceConstructor invoiceConstructor;
	
	private volatile EmailService emailService;

	private volatile AccountService accountService;
	
	private volatile InvoiceService invoiceService;
	
	private volatile InvoiceTransformationService invoiceTransformationService;
	
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
		for (String accountId : accounts) {
			logService.log(LogService.LOG_INFO, "Constructing invoice for " + accountId);
			try {
				final Invoice invoice = invoiceConstructor.constructInvoiceForAccount(day, accountId);
				if (invoice != null) {
					final Optional<? extends Account> account = accountService.getAccountById(accountId);
					final byte[] pdf = invoiceTransformationService.transformToPdf(invoice, account.get());
					writeToFile(invoice.getId() + ".pdf", pdf);
					
					final MultiPartEmail email = emailService.newMultiPartEmail();
					email.addTo(account.get().getEmail(), account.get().getFullName().getFullName());
					email.setFrom("mark@limesco.org", "Mark van Cuijk");
					email.addReplyTo("support@limesco.nl", "Limesco support");
					email.setSubject("Limesco factuur " + invoice.getId());
					
					final String formattedPrice = BigDecimal.valueOf(invoice.getTotalWithTaxes())
							.divide(BigDecimal.valueOf(10000))
							.setScale(2, RoundingMode.HALF_UP)
							.toPlainString()
							.replaceAll("\\.", ",");
					final StringBuilder sb = new StringBuilder();
					sb.append("Dag ").append(account.get().getFullName().getFirstName()).append(",\n\n")
							.append("Sociale conventie schrijft voor dat het na de 6e van januari niet ")
							.append("meer is toegestaan om nieuwjaarswensen uit te wisselen. ")
							.append("Tegen deze conventie in wenst Limesco je het allerbeste toe ")
							.append("in 2013!\n\n")
							.append("Bijgevoegd tref je de laatste factuur van Limesco. ")
							.append("Het totaalbedrag van EUR ").append(formattedPrice)
							.append(" dienst te worden voldaan binnen 14 dagen op rekening ")
							.append("1692.07.587 onder vermelding van factuurnummer ")
							.append(invoice.getId()).append(".\n\n")
							.append("Merk op dat gesprekken naar bestemmingen buiten Nederland ")
							.append("en nummers met afwijkende tarieven nog niet in deze factuur zijn ")
							.append("opgenomen. Deze gesprekken blijven wel in de database bewaard ")
							.append("en worden in een toekomstige factuur meegenomen. ")
							.append("Wij hebben gekozen voor deze aanpak, omdat we nog niet voldoende ")
							.append("vertrouwen hebben dat de afhandeling van deze gesprekken ")
							.append("foutloos verloopt en we willen niet dat jij daar de dupe van wordt. ")
							.append("De bijdrage vaste kosten blijft nog ongewijzigd, omdat we nog ")
							.append("niet de 150 gebruikers hebben bereikt.\n\n")
							.append("Ondertussen zijn wij druk ")
							.append("bezig met de bouw van een nieuwe website. Zodra deze af is ")
							.append("en online staat, gaan we meer publiciteit zoeken om het ")
							.append("aantal gebruikers te verhogen. Omdat de website van de hele community is ")
							.append("zoeken we je hulp. Ben je tevreden met Limesco en vind je het ")
							.append("belangrijk wat wij doen, stuur ons dan een korte tekst (bijv. 2 of 3 korte zinnen) met ")
							.append("je ervaring van Limesco of waarom je jezelf bij de community ")
							.append("hebt aangesloten. Wij zullen je daar erg dankbaar voor zijn!\n\n")
							.append("Heb je vragen over deze factuur? Mail even naar support@limesco.nl ")
							.append("en zorg dat je het factuurnummer ").append(invoice.getId())
							.append(" vermeldt, zodat wij je snel kunnen helpen.\n\n")
							.append("Met vriendelijke groet,\n")
							.append("Mark van Cuijk\n\n");
					email.setMsg(sb.toString());
					
					final EmailAttachment attachment = new EmailAttachment();
					attachment.setPath(invoice.getId() + ".pdf");
					attachment.setDisposition(EmailAttachment.ATTACHMENT);
					attachment.setDescription("Limesco factuur " + invoice.getId());
					attachment.setName(invoice.getId() + ".pdf");
					email.attach(attachment);
					
					email.send();
				}
			} catch (IdAllocationException e) {
				logService.log(LogService.LOG_WARNING, "Failed to allocate ID to invoice for " + accountId);
			} catch (Exception e) {
				logService.log(LogService.LOG_WARNING, "Something went wrong creating invoice for " + accountId);
			}
		}
	}

	private void writeToFile(String filename, byte[] content) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(filename);
			stream.write(content);
		} catch (IOException e) {
			logService.log(LogService.LOG_WARNING, "Failed to write " + filename, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logService.log(LogService.LOG_WARNING, "Failed to close stream for " + filename, e);
				}
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
				logService.log(LogService.LOG_ERROR, "Obtain pricing rule for CDR " + cdr.getSource() + "/" + cdr.getCallId() + " doesn't seem to be applicable");
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
