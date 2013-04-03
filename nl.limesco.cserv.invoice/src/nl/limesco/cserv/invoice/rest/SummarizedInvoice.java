package nl.limesco.cserv.invoice.rest;

import java.util.Calendar;

import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;

public class SummarizedInvoice {

	private String id;
	
	private InvoiceCurrency currency;

	private long totalWithoutTaxes;
	
	private long totalWithTaxes;
	
	private Calendar creationDate;

	public SummarizedInvoice() {
	}
	
	public SummarizedInvoice(Invoice invoice) {
		id = invoice.getId();
		currency = invoice.getCurrency();
		totalWithoutTaxes = invoice.getTotalWithoutTaxes();
		totalWithTaxes = invoice.getTotalWithTaxes();
		creationDate = invoice.getCreationDate();
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
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	
}
