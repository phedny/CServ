package nl.limesco.cserv.invoice.api;

import java.util.List;

public interface ItemLine {

	String getDescription();
	
	List<String> getMultilineDescription();

	long getTotalPrice();
	
	double getTaxRate();

}