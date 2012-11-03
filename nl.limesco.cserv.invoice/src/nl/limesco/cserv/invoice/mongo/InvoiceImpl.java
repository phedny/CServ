package nl.limesco.cserv.invoice.mongo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.Id;
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

public class InvoiceImpl implements Invoice {

	private String id;
	
	private String accountId;
	
	private Calendar creationDate;
	
	private InvoiceCurrency currency;
	
	private List<AbstractItemLine> itemLines = Collections.emptyList();
	
	private List<TaxLineImpl> taxLines = Collections.emptyList();
	
	private long totalWithoutTaxes;
	
	private long totalWithTaxes;
	
	@Id
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	@JsonIgnore
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Calendar creationDate) {
		checkNotNull(creationDate);
		this.creationDate = creationDate;
	}
	
	@JsonProperty("creationDate")
	public Date getCreationDateFromCalendar() {
		if (creationDate == null) {
			return null;
		} else {
			return creationDate.getTime();
		}
	}
	
	public void setCreationDateFromCalendar(Date creationDateCalendar) {
		if (creationDateCalendar == null) {
			this.creationDate = null;
		} else {
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(creationDateCalendar);
			this.creationDate = calendar;
		}
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
