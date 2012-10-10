package nl.limesco.cserv.invoice.api;

public enum InvoiceCurrency {

	EUR(2, 2);

	public final int hiddenDigits;
	
	public final int fractionDigits;
	
	private InvoiceCurrency(int hiddenDigits, int fractionDigits) {
		this.hiddenDigits = hiddenDigits;
		this.fractionDigits = fractionDigits;
	}
	
}
