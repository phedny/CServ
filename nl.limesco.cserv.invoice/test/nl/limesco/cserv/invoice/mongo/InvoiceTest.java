package nl.limesco.cserv.invoice.mongo;

import junit.framework.TestCase;
import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.invoice.api.TaxLine;

import com.google.common.collect.Lists;

public class InvoiceTest extends TestCase {

	private InvoiceImpl invoice = new InvoiceImpl();
	
	public void testInvoiceCanBeEmpty() {
		assertTrue(invoice.isSound());
	}

	public void testInvoiceCanHaveNormalItemLine() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLinesAndTotals();
		assertTrue(invoice.isSound());
		assertEquals(347108, invoice.getTotalWithoutTaxes());
		assertEquals(420001, invoice.getTotalWithTaxes());
	}

	public void testInvoiceCanHaveDurationItemLine() {
		final DurationItemLineImpl itemLine = new DurationItemLineImpl();
		itemLine.setNumberOfCalls(9);
		itemLine.setPricePerCall(400);
		itemLine.setNumberOfSeconds(400);
		itemLine.setPricePerMinute(1000);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLinesAndTotals();
		assertTrue(invoice.isSound());
		assertEquals(10266, invoice.getTotalWithoutTaxes());
		assertEquals(12422, invoice.getTotalWithTaxes());
	}

	public void testInvoiceCanHaveMultipleItemLines() {
		final NormalItemLineImpl itemLine1 = new NormalItemLineImpl();
		itemLine1.setItemPrice(173554);
		itemLine1.setItemCount(2);
		itemLine1.setTaxRate(0.21);
		itemLine1.setTotalPrice();
		
		final DurationItemLineImpl itemLine2 = new DurationItemLineImpl();
		itemLine2.setNumberOfCalls(9);
		itemLine2.setPricePerCall(400);
		itemLine2.setNumberOfSeconds(400);
		itemLine2.setPricePerMinute(1000);
		itemLine2.setTaxRate(0.21);
		itemLine2.setTotalPrice();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine1, itemLine2));
		invoice.setTaxLinesAndTotals();
		assertTrue(invoice.isSound());
		assertEquals(357374, invoice.getTotalWithoutTaxes());
		assertEquals(432423, invoice.getTotalWithTaxes());
	}

	public void testInvoiceCanHaveMultipleItemLinesWithDifferentTaxRates() {
		final NormalItemLineImpl itemLine1 = new NormalItemLineImpl();
		itemLine1.setItemPrice(173554);
		itemLine1.setItemCount(2);
		itemLine1.setTaxRate(0.21);
		itemLine1.setTotalPrice();
		
		final DurationItemLineImpl itemLine2 = new DurationItemLineImpl();
		itemLine2.setNumberOfCalls(9);
		itemLine2.setPricePerCall(400);
		itemLine2.setNumberOfSeconds(400);
		itemLine2.setPricePerMinute(1000);
		itemLine2.setTaxRate(0);
		itemLine2.setTotalPrice();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine1, itemLine2));
		invoice.setTaxLinesAndTotals();
		assertTrue(invoice.isSound());
		assertEquals(357374, invoice.getTotalWithoutTaxes());
		assertEquals(430267, invoice.getTotalWithTaxes());
	}

	public void testInvoiceCannotHaveUnsoundItemLines() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice(173554);
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLinesAndTotals();
		assertEquals(false, invoice.isSound());
	}

	public void testInvoiceCannotHaveUnsoundTaxLines() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		final TaxLineImpl taxLine = new TaxLineImpl();
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount(208177);
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLines(Lists.newArrayList((TaxLine) taxLine));
		invoice.setTotalWithoutTaxes();
		invoice.setTotalWithTaxes();
		assertEquals(false, invoice.isSound());
	}

	public void testInvoiceItemLinesAndTaxLinesEqualAmount() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		final TaxLineImpl taxLine = new TaxLineImpl();
		taxLine.setTaxRate(0.21);
		taxLine.setBaseAmount(991322);
		taxLine.setTaxAmount();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLines(Lists.newArrayList((TaxLine) taxLine));
		invoice.setTotalWithoutTaxes();
		invoice.setTotalWithTaxes();
		assertEquals(false, invoice.isSound());
	}

	public void testInvoiceItemLinesAndTaxLinesEqualTaxRate() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		final TaxLineImpl taxLine = new TaxLineImpl();
		taxLine.setTaxRate(0.06);
		taxLine.setBaseAmount(itemLine.getTotalPrice());
		taxLine.setTaxAmount();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLines(Lists.newArrayList((TaxLine) taxLine));
		invoice.setTotalWithoutTaxes();
		invoice.setTotalWithTaxes();
		assertEquals(false, invoice.isSound());
	}

	public void testInvoiceTotalAmountMustBeSumOfItemLinesAndTaxes() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice();
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLines();
		invoice.setTotalWithoutTaxes();
		invoice.setTotalWithTaxes(173554);
		assertEquals(false, invoice.isSound());
	}
	
}
