package nl.limesco.cserv.invoice.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.ItemLine;

import org.junit.Test;

public class InvoiceBuilderTest {

	private InvoiceBuilderImpl builder = new InvoiceBuilderImpl();
	
	@Test
	public void emptyInvoiceCanBeCreated() {
		final Invoice invoice = builder.build();
		assertTrue(invoice.isSound());
		assertEquals(0, invoice.getTotalWithoutTaxes());
		assertEquals(0, invoice.getTotalWithTaxes());
	}
	
	@Test
	public void invoiceCanHaveAccountId() {
		assertEquals("id", builder.accountId("id").build().getAccountId());
	}
	
	@Test
	public void invoiceCanHaveCurrency() {
		assertEquals(InvoiceCurrency.EUR, builder.currency(InvoiceCurrency.EUR).build().getCurrency());
	}
	
	@Test
	public void invoiceCanHaveNormalItemLine() {
		final ItemLine itemLine = builder.normalItemLine("desc", 4, 14, 0.1).build().getItemLines().get(0);
		assertEquals("desc", itemLine.getDescription());
		assertEquals(56, itemLine.getTotalPrice());
	}
	
	@Test
	public void invoiceCanHaveDurationItemLine() {
		final ItemLine itemLine = builder.durationItemLine("desc", 4, 10, 2, 90, 0.1).build().getItemLines().get(0);
		assertEquals("desc", itemLine.getDescription());
		assertEquals(23, itemLine.getTotalPrice());
	}
	
	@Test
	public void complexInvoiceCanBeCreated() {
		final Invoice invoice = builder.id("id").accountId("acc")
				.currency(InvoiceCurrency.EUR)
				.normalItemLine("Item 1", 1, 15, 0.21)
				.normalItemLine("Item 2", 2, 4, 0.21)
				.normalItemLine("Item 3", 1, 20, 0.06)
				.durationItemLine("Item 4", 4, 10, 2, 90, 0.21)
				.durationItemLine("Item 5", 4, 35, 1, 12, 0.21)
				.build();
		
		assertTrue(invoice.isSound());
		assertEquals(5, invoice.getItemLines().size());
		assertEquals(2, invoice.getTaxLines().size());
		assertEquals(77, invoice.getTotalWithoutTaxes());
		assertEquals(90, invoice.getTotalWithTaxes());
	}
	
}
