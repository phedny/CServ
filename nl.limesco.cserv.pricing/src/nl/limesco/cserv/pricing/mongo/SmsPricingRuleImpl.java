package nl.limesco.cserv.pricing.mongo;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.SmsApplicationConstraints;
import nl.limesco.cserv.pricing.api.SmsPricing;
import nl.limesco.cserv.pricing.api.SmsPricingRule;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class SmsPricingRuleImpl extends AbstractPricingRuleImpl implements SmsPricingRule {
	
	private SmsApplicationConstraintsImpl applicability;
	
	private SmsPricingImpl price;
	
	private SmsPricingImpl cost;
	
	@Override
	@JsonIgnore
	public SmsApplicationConstraints getApplicability() {
		return applicability;
	}
	
	@JsonProperty("applicability")
	public SmsApplicationConstraintsImpl getApplicabilityImpl() {
		return applicability;
	}
	
	public void setApplicabilityImpl(SmsApplicationConstraintsImpl applicability) {
		this.applicability = applicability;
	}

	@Override
	@JsonIgnore
	public SmsPricing getPrice() {
		return price;
	}
	
	@JsonProperty("price")
	public SmsPricingImpl getPriceImpl() {
		return price;
	}
	
	public void setPriceImpl(SmsPricingImpl price) {
		this.price = price;
	}

	@Override
	@JsonIgnore
	public SmsPricing getCost() {
		return cost;
	}
	
	@JsonProperty("cost")
	public SmsPricingImpl getCostImpl() {
		return cost;
	}
	
	public void setCostImpl(SmsPricingImpl cost) {
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
