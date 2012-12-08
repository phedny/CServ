package nl.limesco.cserv.pricing.mongo;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.DataApplicationConstraints;
import nl.limesco.cserv.pricing.api.DataPricing;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class DataPricingRuleImpl extends AbstractPricingRuleImpl implements DataPricingRule {
	
	private DataApplicationConstraintsImpl applicability;
	
	private DataPricingImpl price;
	
	private DataPricingImpl cost;
	
	@Override
	@JsonIgnore
	public DataApplicationConstraints getApplicability() {
		return applicability;
	}
	
	@JsonProperty("applicability")
	public DataApplicationConstraintsImpl getApplicabilityImpl() {
		return applicability;
	}
	
	public void setApplicabilityImpl(DataApplicationConstraintsImpl applicability) {
		this.applicability = applicability;
	}

	@Override
	@JsonIgnore
	public DataPricing getPrice() {
		return price;
	}
	
	@JsonProperty("price")
	public DataPricingImpl getPriceImpl() {
		return price;
	}
	
	public void setPriceImpl(DataPricingImpl price) {
		this.price = price;
	}

	@Override
	@JsonIgnore
	public DataPricing getCost() {
		return cost;
	}
	
	@JsonProperty("cost")
	public DataPricingImpl getCostImpl() {
		return cost;
	}
	
	public void setCostImpl(DataPricingImpl cost) {
		this.cost = cost;
	}

	@Override
	public long getPriceForCdr(Cdr cdr) throws PricingRuleNotApplicableException {
		if (!applicability.isApplicable(cdr)) {
			throw new PricingRuleNotApplicableException();
		}
		return price.getForCdr(cdr);
	}

	@Override
	public long getCostForCdr(Cdr cdr) throws PricingRuleNotApplicableException {
		if (!applicability.isApplicable(cdr)) {
			throw new PricingRuleNotApplicableException();
		}
		return cost.getForCdr(cdr);
	}

}
