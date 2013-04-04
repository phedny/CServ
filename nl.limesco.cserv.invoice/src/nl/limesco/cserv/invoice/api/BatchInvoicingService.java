package nl.limesco.cserv.invoice.api;

import java.util.Calendar;

public interface BatchInvoicingService {

	void runBatch(Calendar day);
	
	// HACK: Use the BatchInvoicingService for computing the prices of previously unpriced CDR's
	// This is necessary before invoice generation.
	void computePricingForUnpricedCdrs();
	
}
