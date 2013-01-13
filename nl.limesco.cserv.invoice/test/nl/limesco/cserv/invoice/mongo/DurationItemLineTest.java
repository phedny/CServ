package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class DurationItemLineTest {

	private DurationItemLineImpl itemLine = new DurationItemLineImpl();
	
	@Test
	public void itemLineCanBeZero() {
		assertTrue(itemLine.isSound());
	}
	
	@Test
	public void itemLineCanHaveCalls() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(3600, itemLine.getTotalPrice());
	}
	
	@Test
	public void itemLineCanHaveSeconds() {
		itemLine.setNumberOfSeconds(400);
		itemLine.setPricePerMinute(1000);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(6666, itemLine.getTotalPrice());
	}
	
	@Test
	public void itemLineCanHaveCallsAndSeconds() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setNumberOfSeconds(400);
		itemLine.setPricePerMinute(1000);
		itemLine.setTotalPrice();
		assertTrue(itemLine.isSound());
		assertEquals(10266, itemLine.getTotalPrice());
	}

	@Test
	public void unsoundItemLineIsDetected() {
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setTotalPrice(400);
		assertEquals(false, itemLine.isSound());
	}

	@Test
	public void emptyMultilineDescriptionIsCopiedFromDescription() {
		itemLine.setDescription("1, 2, 3, TEST!");
		assertEquals(1, itemLine.getMultilineDescription().size());
		assertEquals("1, 2, 3, TEST!", itemLine.getMultilineDescription().get(0));
	}

	@Test
	public void nonEmptyMultilineDescriptionIsNotCopiedFromDescription() {
		itemLine.setNullableMultilineDescription(Arrays.asList("1", "2", "3", "TEST"));
		itemLine.setDescription("1, 2, 3, TEST!");
		assertEquals(4, itemLine.getMultilineDescription().size());
	}
	
}
