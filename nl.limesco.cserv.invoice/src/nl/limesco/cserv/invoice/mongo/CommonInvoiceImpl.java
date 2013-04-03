package nl.limesco.cserv.invoice.mongo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.invoice.api.TaxLine;

public class CommonInvoiceImpl {
	
	public String id;
	
	public String accountId;
	
	public String correspondingInvoiceId;
	
	public Calendar creationDate;
	
	public InvoiceCurrency currency;
	
	public List<AbstractItemLine> itemLines = Collections.emptyList();
	
	public List<TaxLineImpl> taxLines = Collections.emptyList();
	
	public long totalWithoutTaxes;
	
	public long totalWithTaxes;
	
	public void setItemLines(List<ItemLine> itemLines) {
		final List<AbstractItemLine> copy = Lists.newArrayList();
		for (ItemLine itemLine : itemLines) {
			copy.add((AbstractItemLine) itemLine);
		}
		this.itemLines = copy;
	}

	public void setTaxLines(List<TaxLine> taxLines) {
		final List<TaxLineImpl> copy = Lists.newArrayList();
		for (TaxLine taxLine : taxLines) {
			copy.add((TaxLineImpl) taxLine);
		}
		this.taxLines = copy;
	}
	
	public void setTaxLines() {
		final Map<Double, TaxLineImpl> taxLineMap = Maps.newTreeMap();
		for (ItemLine itemLine : itemLines) {
			final Double taxRate = Double.valueOf(itemLine.getTaxRate());
			TaxLineImpl taxLine = taxLineMap.get(taxRate);
			if (taxLine == null) {
				taxLine = new TaxLineImpl();
				taxLine.setTaxRate(itemLine.getTaxRate());
				taxLine.setBaseAmount(itemLine.getTotalPrice());
				taxLineMap.put(taxRate, taxLine);
			} else {
				taxLine.setBaseAmount(taxLine.getBaseAmount() + itemLine.getTotalPrice());
			}
		}
		
		final ArrayList<TaxLineImpl> taxLines = Lists.newArrayList(taxLineMap.values());
		for (TaxLineImpl taxLine : taxLines) {
			taxLine.setTaxAmount();
		}
		this.taxLines = taxLines;
	}
	
	public void setTotalWithoutTaxes() {
		long sum = 0;
		for (ItemLine itemLine : itemLines) {
			sum += itemLine.getTotalPrice();
		}
		totalWithoutTaxes = sum;
	}
	
	public void setTotalWithTaxes() {
		long sum = totalWithoutTaxes;
		for (TaxLine taxLine : taxLines) {
			sum += taxLine.getTaxAmount();
		}
		totalWithTaxes = sum;
	}
	
	public void setTaxLinesAndTotals() {
		setTaxLines();
		setTotalWithoutTaxes();
		setTotalWithTaxes();
	}
	
	public boolean isSound() {
		// Compute sum of tax lines
		long sumOfTaxLines = 0;
		long sumOfTaxAmounts = 0;
		final Map<Double, Long> taxRateMap = Maps.newHashMap();
		for (TaxLineImpl taxLine : taxLines) {
			if (!taxLine.isSound()) {
				return false;
			}
			sumOfTaxLines += taxLine.getBaseAmount();
			sumOfTaxAmounts += taxLine.getTaxAmount();
			taxRateMap.put(Double.valueOf(taxLine.getTaxRate()), Long.valueOf(taxLine.getBaseAmount()));
		}

		// Compute sum of item lines and prepare tax line coverage
		long sumOfItemLines = 0;
		for (AbstractItemLine itemLine : itemLines) {
			if (!itemLine.isSound()) {
				return false;
			}
			sumOfItemLines += itemLine.getTotalPrice();
			
			try {
				final Double taxRate = Double.valueOf(itemLine.getTaxRate());
				taxRateMap.put(taxRate, taxRateMap.get(taxRate).longValue() - itemLine.getTotalPrice());
			} catch (NullPointerException e) {
				// This happens when the itemLine has a tax rate for which no matching taxLine exists
				return false;
			}
		}
		
		// Test tax line coverage
		for (Long taxAmount : taxRateMap.values()) {
			if (taxAmount.longValue() != 0) {
				return false;
			}
		}
		
		if (sumOfItemLines != totalWithoutTaxes || sumOfTaxLines != totalWithoutTaxes) {
			return false;
		} else if (sumOfTaxLines + sumOfTaxAmounts != totalWithTaxes) {
			return false;
		}
		
		return true;
	}
}
