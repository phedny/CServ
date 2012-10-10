package nl.limesco.cserv.invoice.api;

public interface TaxLine {

	long getBaseAmount();

	long getTaxAmount();

	double getTaxRate();

}