package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaxLineTest {

	private TaxLineImpl taxLine = new TaxLineImpl();
	
	@Test
	public void taxLineCanBeZero() {
		assertTrue(taxLine.isSound());
	}
	
	@Test
	public void taxLineCanBeSet() {
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount();
		assertTrue(taxLine.isSound());
		assertEquals(208178, taxLine.getTaxAmount());
	}
	
	@Test
	public void unsoundTaxLineIsDetected() {
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount(208177);
		assertEquals(false, taxLine.isSound());
	}
	
}
