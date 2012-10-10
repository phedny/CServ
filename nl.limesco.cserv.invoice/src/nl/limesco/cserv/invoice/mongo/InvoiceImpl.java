package nl.limesco.cserv.invoice.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.invoice.api.TaxLine;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class InvoiceImpl implements Invoice {

	private String id;
	
	private String sequentialId;
	
	private String accountId;
	
	private InvoiceCurrency currency;
	
	private List<AbstractItemLine> itemLines = Collections.emptyList();
	
	private List<TaxLineImpl> taxLines = Collections.emptyList();
	
	private long totalWithoutTaxes;
	
	private long totalWithTaxes;
	
	@ObjectId
	@JsonProperty("_id")
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getSequentialId() {
		return sequentialId;
	}

	public void setSequentialId(String sequentialId) {
		this.sequentialId = sequentialId;
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public InvoiceCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(InvoiceCurrency currency) {
		this.currency = currency;
	}

	@Override
	@JsonSerialize(contentAs = AbstractItemLine.class)
	@JsonDeserialize(contentAs = AbstractItemLine.class)
	public List<? extends ItemLine> getItemLines() {
		return Collections.unmodifiableList(itemLines);
	}

	public void setItemLines(List<ItemLine> itemLines) {
		final List<AbstractItemLine> copy = Lists.newArrayList();
		for (ItemLine itemLine : itemLines) {
			copy.add((AbstractItemLine) itemLine);
		}
		this.itemLines = copy;
	}

	@Override
	@JsonSerialize(contentAs = TaxLineImpl.class)
	@JsonDeserialize(contentAs = TaxLineImpl.class)
	public List<? extends TaxLine> getTaxLines() {
		return Collections.unmodifiableList(taxLines);
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

	@Override
	public long getTotalWithoutTaxes() {
		return totalWithoutTaxes;
	}

	public void setTotalWithoutTaxes(long totalWithoutTaxes) {
		this.totalWithoutTaxes = totalWithoutTaxes;
	}
	
	public void setTotalWithoutTaxes() {
		long sum = 0;
		for (ItemLine itemLine : itemLines) {
			sum += itemLine.getTotalPrice();
		}
		totalWithoutTaxes = sum;
	}

	@Override
	public long getTotalWithTaxes() {
		return totalWithTaxes;
	}

	public void setTotalWithTaxes(long totalWithTaxes) {
		this.totalWithTaxes = totalWithTaxes;
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

	@Override
	@JsonIgnore
	public boolean isSound() {
		
		// Compute sum of item lines
		long sumOfItemLines = 0;
		for (AbstractItemLine itemLine : itemLines) {
			if (!itemLine.isSound()) {
				return false;
			}
			sumOfItemLines += itemLine.getTotalPrice();
		}
		
		// Compute sum of tax lines
		long sumOfTaxLines = 0;
		long sumOfTaxAmounts = 0;
		final Set<Double> seenTaxRates = Sets.newHashSet();
		for (TaxLineImpl taxLine : taxLines) {
			if (!taxLine.isSound() || !seenTaxRates.add(Double.valueOf(taxLine.getTaxRate()))) {
				return false;
			}
			sumOfTaxLines += taxLine.getBaseAmount();
			sumOfTaxAmounts += taxLine.getTaxAmount();
		}
		
		if (sumOfItemLines != totalWithoutTaxes || sumOfTaxLines != totalWithoutTaxes) {
			return false;
		} else if (sumOfTaxLines + sumOfTaxAmounts != totalWithTaxes) {
			return false;
		}
		
		return true;
	}

}
