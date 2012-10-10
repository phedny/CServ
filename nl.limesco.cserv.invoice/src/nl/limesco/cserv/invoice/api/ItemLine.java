package nl.limesco.cserv.invoice.api;

public interface ItemLine {

	String getDescription();

	long getTotalPrice();
	
	double getTaxRate();

}