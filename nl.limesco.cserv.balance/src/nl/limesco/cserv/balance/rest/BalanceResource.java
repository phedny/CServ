package nl.limesco.cserv.balance.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentService;
import nl.limesco.cserv.payment.api.PaymentStatus;

import com.google.common.collect.Maps;

public class BalanceResource {

	private final InvoiceService invoiceService;
	
	private final PaymentService paymentService;
	
	private final Account account;
	
	private final boolean admin;
	
	public BalanceResource(InvoiceService invoiceService, PaymentService paymentService, Account account, boolean admin) {
		this.invoiceService = invoiceService;
		this.paymentService = paymentService;
		this.account = account;
		this.admin = admin;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<InvoiceCurrency, Long> getBalance() {
		final Map<InvoiceCurrency, Long> balances = Maps.newHashMap();
		
		for (Payment payment : paymentService.getPaymentsByAccountId(account.getId())) {
			final PaymentStatus status = payment.getStatus();
			if (PaymentStatus.COMPLETED.equals(status) || PaymentStatus.VERIFIED.equals(status)) {
				final InvoiceCurrency currency = payment.getCurrency();
				if (balances.containsKey(currency)) {
					balances.put(currency, Long.valueOf(balances.get(currency).longValue() + payment.getAmount()));
				} else {
					balances.put(currency, Long.valueOf(payment.getAmount()));
				}
			}
		}
		
		for (Invoice invoice : invoiceService.getInvoicesByAccountId(account.getId())) {
			final InvoiceCurrency currency = invoice.getCurrency();
			if (balances.containsKey(currency)) {
				balances.put(currency, Long.valueOf(balances.get(currency).longValue() - invoice.getTotalWithTaxes()));
			} else {
				balances.put(currency, Long.valueOf(-invoice.getTotalWithTaxes()));
			}
		}
		
		return balances;
	}
}
