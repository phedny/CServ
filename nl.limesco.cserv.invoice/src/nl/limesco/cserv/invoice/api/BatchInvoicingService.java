package nl.limesco.cserv.invoice.api;

import java.util.Calendar;

public interface BatchInvoicingService {

	void runBatch(Calendar day);
	
}
