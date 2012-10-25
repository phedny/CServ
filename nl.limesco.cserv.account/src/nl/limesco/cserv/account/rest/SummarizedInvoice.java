package nl.limesco.cserv.account.rest;

import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;

public class SummarizedInvoice {

	private String id;
	
	private InvoiceCurrency currency;

	private long totalWithoutTaxes;
	
	private long totalWithTaxes;

	public SummarizedInvoice() {
	}
	
	public SummarizedInvoice(Invoice invoice) {
		id = invoice.getId();
		currency = invoice.getCurrency();
		totalWithoutTaxes = invoice.getTotalWithoutTaxes();
		totalWithTaxes = invoice.getTotalWithTaxes();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InvoiceCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(InvoiceCurrency currency) {
		this.currency = currency;
	}

	public long getTotalWithoutTaxes() {
		return totalWithoutTaxes;
	}

	public void setTotalWithoutTaxes(long totalWithoutTaxes) {
		this.totalWithoutTaxes = totalWithoutTaxes;
	}

	public long getTotalWithTaxes() {
		return totalWithTaxes;
	}

	public void setTotalWithTaxes(long totalWithTaxes) {
		this.totalWithTaxes = totalWithTaxes;
	}
	
}
