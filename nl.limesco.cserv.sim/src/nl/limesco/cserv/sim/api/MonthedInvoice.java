package nl.limesco.cserv.sim.api;

public class MonthedInvoice {
	private int year; // from Calendar.get(Calendar.YEAR)
	private int month; // from Calendar.get(Calendar.MONTH)
	private String invoiceId;
	
	public MonthedInvoice() {
	}
	
	public MonthedInvoice(int year, int month, String invoiceId) {
		this.year = year;
		this.month = month;
		this.invoiceId = invoiceId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
}
