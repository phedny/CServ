package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
