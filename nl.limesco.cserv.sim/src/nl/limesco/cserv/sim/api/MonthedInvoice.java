package nl.limesco.cserv.sim.api;

public class MonthedInvoice {
	public int year; // from Calendar.get(Calendar.YEAR)
	public int month; // from Calendar.get(Calendar.MONTH)
	public String invoiceId;
	
	public MonthedInvoice() {
	}
	
	public MonthedInvoice(int year, int month, String invoiceId) {
		this.year = year;
		this.month = month;
		this.invoiceId = invoiceId;
	}
}
