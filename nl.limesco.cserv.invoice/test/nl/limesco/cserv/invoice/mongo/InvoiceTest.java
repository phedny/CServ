package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.invoice.api.TaxLine;

import org.junit.Test;

import com.google.common.collect.Lists;

public class InvoiceTest {

	private InvoiceImpl invoice = new InvoiceImpl();
	
	@Test
	public void invoiceCanBeEmpty() {
		assertTrue(invoice.isSound());
	}

	@Test
	public void invoiceCanHaveNormalItemLine() {
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

	@Test
	public void invoiceCanHaveDurationItemLine() {
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

	@Test
	public void invoiceCanHaveMultipleItemLines() {
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

	@Test
	public void invoiceCanHaveMultipleItemLinesWithDifferentTaxRates() {
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

	@Test
	public void invoiceCannotHaveUnsoundItemLines() {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setItemPrice(173554);
		itemLine.setItemCount(2);
		itemLine.setTaxRate(0.21);
		itemLine.setTotalPrice(173554);
		
		invoice.setItemLines(Lists.newArrayList((ItemLine) itemLine));
		invoice.setTaxLinesAndTotals();
		assertEquals(false, invoice.isSound());
	}

	@Test
	public void invoiceCannotHaveUnsoundTaxLines() {
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

	@Test
	public void invoiceItemLinesAndTaxLinesEqualAmount() {
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

	@Test
	public void invoiceItemLinesAndTaxLinesEqualTaxRate() {
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

	@Test
	public void invoiceTotalAmountMustBeSumOfItemLinesAndTaxes() {
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
	
	@Test
	public void invoiceCreationDateCanBeSetFromCalendar() {
		final Calendar cal = Calendar.getInstance();
		invoice.setCreationDateFromCalendar(cal.getTime());
		
		assertEquals(true, (invoice.getCreationDate() != null));
	}
	
}
