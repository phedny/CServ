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
	
	CommonInvoiceImpl commonInvoiceImpl = new CommonInvoiceImpl();
	
	void setCommonInvoiceImpl(CommonInvoiceImpl implementation) {
		this.commonInvoiceImpl = implementation;
	}
	
	@Id
	@Override
	public String getId() {
		return commonInvoiceImpl.id;
	}

	public void setId(String id) {
		commonInvoiceImpl.id = id;
	}

	@Override
	public String getAccountId() {
		return commonInvoiceImpl.accountId;
	}

	public void setAccountId(String accountId) {
		commonInvoiceImpl.accountId = accountId;
	}
	
	@Override
	public String getCostInvoiceId() {
		return commonInvoiceImpl.correspondingInvoiceId;
	}
	
	public void setCostInvoiceId(String invoiceId) {
		commonInvoiceImpl.correspondingInvoiceId = invoiceId;
	}

	@Override
	@JsonIgnore
	public Calendar getCreationDate() {
		return commonInvoiceImpl.creationDate;
	}
	
	public void setCreationDate(Calendar creationDate) {
		checkNotNull(creationDate);
		commonInvoiceImpl.creationDate = creationDate;
	}
	
	@JsonProperty("creationDate")
	public Date getCreationDateFromCalendar() {
		if (commonInvoiceImpl.creationDate == null) {
			return null;
		} else {
			return commonInvoiceImpl.creationDate.getTime();
		}
	}
	
	public void setCreationDateFromCalendar(Date creationDateCalendar) {
		if (creationDateCalendar == null) {
			commonInvoiceImpl.creationDate = null;
		} else {
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(creationDateCalendar);
			commonInvoiceImpl.creationDate = calendar;
		}
	}
	
	@Override
	public InvoiceCurrency getCurrency() {
		return commonInvoiceImpl.currency;
	}

	public void setCurrency(InvoiceCurrency currency) {
		commonInvoiceImpl.currency = currency;
	}

	@Override
	@JsonSerialize(contentAs = AbstractItemLine.class)
	@JsonDeserialize(contentAs = AbstractItemLine.class)
	public List<? extends ItemLine> getItemLines() {
		return Collections.unmodifiableList(commonInvoiceImpl.itemLines);
	}

	public void setItemLines(List<ItemLine> itemLines) {
		commonInvoiceImpl.setItemLines(itemLines);
	}

	@Override
	@JsonSerialize(contentAs = TaxLineImpl.class)
	@JsonDeserialize(contentAs = TaxLineImpl.class)
	public List<? extends TaxLine> getTaxLines() {
		return Collections.unmodifiableList(commonInvoiceImpl.taxLines);
	}

	public void setTaxLines(List<TaxLine> taxLines) {
		commonInvoiceImpl.setTaxLines(taxLines);
	}

	public void setTaxLines() {
		commonInvoiceImpl.setTaxLines();
	}
	
	@Override
	public long getTotalWithoutTaxes() {
		return commonInvoiceImpl.totalWithoutTaxes;
	}

	public void setTotalWithoutTaxes(long totalWithoutTaxes) {
		commonInvoiceImpl.totalWithoutTaxes = totalWithoutTaxes;
	}
	
	public void setTotalWithoutTaxes() {
		commonInvoiceImpl.setTotalWithoutTaxes();
	}

	@Override
	public long getTotalWithTaxes() {
		return commonInvoiceImpl.totalWithTaxes;
	}

	public void setTotalWithTaxes(long totalWithTaxes) {
		commonInvoiceImpl.totalWithTaxes = totalWithTaxes;
	}
	
	public void setTotalWithTaxes() {
		commonInvoiceImpl.setTotalWithTaxes();
	}
	
	public void setTaxLinesAndTotals() {
		commonInvoiceImpl.setTaxLinesAndTotals();
	}

	@Override
	@JsonIgnore
	public boolean isSound() {
		return commonInvoiceImpl.isSound();
	}

}
