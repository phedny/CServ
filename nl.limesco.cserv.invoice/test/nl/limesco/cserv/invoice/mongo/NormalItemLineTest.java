package nl.limesco.cserv.invoice.mongo;

import junit.framework.TestCase;

public class NormalItemLineTest extends TestCase {

	private NormalItemLineImpl itemLine = new NormalItemLineImpl();
	
	public void testItemLineCanBeZero() {
		assertTrue(itemLine.isSound());
	}
	
	public void testItemLineCanBeSet() {
		itemLine.setItemCount(2);
		itemLine.setItemPrice(21);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(42, itemLine.getTotalPrice());
	}
	
	public void testUnsoundItemLineIsDetected() {
		itemLine.setItemCount(2);
		itemLine.setItemCount(21);
		itemLine.setTotalPrice(14);
		assertEquals(false, itemLine.isSound());
	}
	
}
