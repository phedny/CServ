package nl.limesco.cserv.invoice.mongo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nl.limesco.cserv.invoice.api.CostInvoice;
import nl.limesco.cserv.invoice.api.DurationItemLine;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.invoice.api.NormalItemLine;

public class InvoiceBuilderImpl implements InvoiceBuilder {

	private final CommonInvoiceImpl invoice;
	
	private final List<ItemLine> itemLines;
	
	public InvoiceBuilderImpl() {
		invoice = new CommonInvoiceImpl();
		itemLines = new ArrayList<ItemLine>();
	}
	
	@Override
	public InvoiceBuilder id(String id) {
		invoice.id = id;
		return this;
	}

	@Override
	public InvoiceBuilder accountId(String accountId) {
		invoice.accountId = accountId;
		return this;
	}

	@Override
	public InvoiceBuilder creationDate(Calendar creationDate) {
		invoice.creationDate = creationDate;
		return this;
	}

	@Override
	public InvoiceBuilder currency(InvoiceCurrency currency) {
		invoice.currency = currency;
		return this;
	}

	@Override
	public InvoiceBuilder itemLine(ItemLine itemLine) {
		if (itemLine instanceof NormalItemLine) {
			return normalItemLine((NormalItemLine) itemLine);
		} else if (itemLine instanceof DurationItemLine) {
			return durationItemLine((DurationItemLine) itemLine);
		} else {
			return null;
		}
	}
	
	private InvoiceBuilder normalItemLine(NormalItemLine origItemLine) {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setDescription(origItemLine.getDescription());
		itemLine.setItemCount(origItemLine.getItemCount());
		itemLine.setItemPrice(origItemLine.getItemPrice());
		itemLine.setTaxRate(origItemLine.getTaxRate());
		itemLine.setTotalPrice();
		itemLines.add(itemLine);
		return this;
	}

	@Override
	public InvoiceBuilder normalItemLine(String description, long itemCount, long itemPrice, double taxRate) {
		return normalItemLine(description, null, itemCount, itemPrice, taxRate);
	}
	
	@Override
	public InvoiceBuilder normalItemLine(String description, List<String> multilineDescription, long itemCount, long itemPrice, double taxRate) {
		final NormalItemLineImpl itemLine = new NormalItemLineImpl();
		itemLine.setDescription(description);
		itemLine.setNullableMultilineDescription(multilineDescription);
		itemLine.setItemCount(itemCount);
		itemLine.setItemPrice(itemPrice);
		itemLine.setTaxRate(taxRate);
		itemLine.setTotalPrice();
		itemLines.add(itemLine);
		return this;
	}

	private InvoiceBuilder durationItemLine(DurationItemLine origItemLine) {
		final DurationItemLineImpl itemLine = new DurationItemLineImpl();
		itemLine.setDescription(origItemLine.getDescription());
		itemLine.setPricePerCall(origItemLine.getPricePerCall());
		itemLine.setPricePerMinute(origItemLine.getPricePerMinute());
		itemLine.setNumberOfCalls(origItemLine.getNumberOfCalls());
		itemLine.setNumberOfSeconds(origItemLine.getNumberOfSeconds());
		itemLine.setTaxRate(origItemLine.getTaxRate());
		itemLine.setTotalPrice();
		itemLines.add(itemLine);
		return this;
	}

	@Override
	public InvoiceBuilder durationItemLine(String description, long pricePerCall, long pricePerMinute, long numberOfCalls, long numberOfSeconds, double taxRate) {
		return durationItemLine(description, null, pricePerCall, pricePerMinute, numberOfCalls, numberOfSeconds, taxRate);
	}

	@Override
	public InvoiceBuilder durationItemLine(String description, List<String> multilineDescription, long pricePerCall, long pricePerMinute, long numberOfCalls, long numberOfSeconds, double taxRate) {
		final DurationItemLineImpl itemLine = new DurationItemLineImpl();
		itemLine.setDescription(description);
		itemLine.setNullableMultilineDescription(multilineDescription);
		itemLine.setPricePerCall(pricePerCall);
		itemLine.setPricePerMinute(pricePerMinute);
		itemLine.setNumberOfCalls(numberOfCalls);
		itemLine.setNumberOfSeconds(numberOfSeconds);
		itemLine.setTaxRate(taxRate);
		itemLine.setTotalPrice();
		itemLines.add(itemLine);
		return this;
	}

	private void finalizeInvoice() {
		invoice.setItemLines(itemLines);
		invoice.setTaxLinesAndTotals();
		assert invoice.isSound();
	}
	
	@Override
	public Invoice buildInvoice() {
		finalizeInvoice();
		InvoiceImpl finalInvoice = new InvoiceImpl();
		finalInvoice.setCommonInvoiceImpl(invoice);
		return finalInvoice;
	}
	
	@Override
	public CostInvoice buildCostInvoice() {
		finalizeInvoice();
		CostInvoiceImpl finalInvoice = new CostInvoiceImpl();
		finalInvoice.setCommonInvoiceImpl(invoice);
		return finalInvoice;
	}

}
