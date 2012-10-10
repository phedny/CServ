package nl.limesco.cserv.invoice.mongo;

import junit.framework.TestCase;

public class DurationItemLineTest extends TestCase {

	private DurationItemLineImpl itemLine = new DurationItemLineImpl();
	
	public void testItemLineCanBeZero() {
		assertTrue(itemLine.isSound());
	}
	
	public void testItemLineCanHaveCalls() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(3600, itemLine.getTotalPrice());
	}
	
	public void testItemLineCanHaveSeconds() {
		itemLine.setNumberOfSeconds(400);
		itemLine.setPricePerMinute(1000);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(6666, itemLine.getTotalPrice());
	}
	
	public void testItemLineCanHaveCallsAndSeconds() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setNumberOfSeconds(400);
		itemLine.setPricePerMinute(1000);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(10266, itemLine.getTotalPrice());
	}

	public void testUnsoundItemLineIsDetected() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setTotalPrice(400);
		assertEquals(false, itemLine.isSound());
	}
	
}
