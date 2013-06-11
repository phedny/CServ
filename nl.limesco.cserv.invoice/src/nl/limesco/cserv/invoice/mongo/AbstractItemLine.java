package nl.limesco.cserv.invoice.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.limesco.cserv.invoice.api.ItemLine;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = NormalItemLineImpl.class, name = "normal"),
	@JsonSubTypes.Type(value = DurationItemLineImpl.class, name = "duration"),
	@JsonSubTypes.Type(value = QueuedItemLineImpl.class, name = "queued")
})
public abstract class AbstractItemLine implements ItemLine {
	
	private String description;
	
	private List<String> multilineDescription;
	
	private long totalPrice;
	
	private double taxRate;

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	@JsonIgnore
	public List<String> getMultilineDescription() {
		if (multilineDescription != null) {
			return Collections.unmodifiableList(multilineDescription);
		} else {
			return Collections.singletonList(description);
		}
	}
	
	@JsonProperty("multilineDescription")
	public List<String> getNullableMultilineDescription() {
		return multilineDescription;
	}
	
	public void setNullableMultilineDescription(List<String> multilineDescription) {
		if (multilineDescription == null) {
			this.multilineDescription = null;
		} else {
			final List<String> copy = new ArrayList<String>();
			copy.addAll(multilineDescription);
			this.multilineDescription = copy;
		}
	}

	@Override
	public long getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setTotalPrice() {
		totalPrice = computeTotalPrice();
	}
	
	@Override
	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	protected abstract long computeTotalPrice();
	
	@JsonIgnore
	public boolean isSound() {
		return computeTotalPrice() == totalPrice;
	}
	
}
