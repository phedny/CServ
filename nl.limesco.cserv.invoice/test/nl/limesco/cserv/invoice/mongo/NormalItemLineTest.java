package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NormalItemLineTest {

	private NormalItemLineImpl itemLine = new NormalItemLineImpl();
	
	@Test
	public void itemLineCanBeZero() {
		assertTrue(itemLine.isSound());
	}
	
	@Test
	public void itemLineCanBeSet() {
		itemLine.setItemCount(2);
		itemLine.setItemPrice(21);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(42, itemLine.getTotalPrice());
	}
	
	@Test
	public void unsoundItemLineIsDetected() {
		itemLine.setItemCount(2);
		itemLine.setItemCount(21);
		itemLine.setTotalPrice(14);
		assertEquals(false, itemLine.isSound());
	}
	
}
