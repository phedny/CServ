package nl.limesco.cserv.invoice.mongo;

import junit.framework.TestCase;

public class TaxLineTest extends TestCase {

	private TaxLineImpl taxLine = new TaxLineImpl();
	
	public void testTaxLineCanBeZero() {
		assertTrue(taxLine.isSound());
	}
	
	public void testTaxLineCanBeSet() {
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount();
		assertTrue(taxLine.isSound());
		assertEquals(208178, taxLine.getTaxAmount());
	}
	
	public void unsoundTaxLineIsDetected() {
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount(208177);
		assertEquals(false, taxLine.isSound());
	}
	
}
