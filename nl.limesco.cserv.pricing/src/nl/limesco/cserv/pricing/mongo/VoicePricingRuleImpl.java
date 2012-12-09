package nl.limesco.cserv.pricing.mongo;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.VoiceApplicationConstraints;
import nl.limesco.cserv.pricing.api.VoicePricing;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class VoicePricingRuleImpl extends AbstractPricingRuleImpl implements VoicePricingRule {
	
	private VoiceApplicationConstraintsImpl applicability;
	
	private VoicePricingImpl price;
	
	private VoicePricingImpl cost;
	
	@Override
	@JsonIgnore
	public VoiceApplicationConstraints getApplicability() {
		return applicability;
	}
	
	@JsonProperty("applicability")
	public VoiceApplicationConstraintsImpl getApplicabilityImpl() {
		return applicability;
	}
	
	public void setApplicabilityImpl(VoiceApplicationConstraintsImpl applicability) {
		this.applicability = applicability;
	}

	@Override
	@JsonIgnore
	public VoicePricing getPrice() {
		return price;
	}
	
	@JsonProperty("price")
	public VoicePricingImpl getPriceImpl() {
		return price;
	}
	
	public void setPriceImpl(VoicePricingImpl price) {
		this.price = price;
	}

	@Override
	@JsonIgnore
	public VoicePricing getCost() {
		return cost;
	}
	
	@JsonProperty("cost")
	public VoicePricingImpl getCostImpl() {
		return cost;
	}
	
	public void setCostImpl(VoicePricingImpl cost) {
		this.cost = cost;
	}

	@Override
	public long getPriceForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException {
		if (!((VoiceCdr) cdr).isConnected()) {
			return 0;
		}
		if (!applicability.isApplicable(cdr, callConnectivityType)) {
			throw new PricingRuleNotApplicableException();
		}
		return price.getForCdr(cdr);
	}

	@Override
	public long getCostForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException {
		if (!((VoiceCdr) cdr).isConnected()) {
			return 0;
		}
		if (!applicability.isApplicable(cdr, callConnectivityType)) {
			throw new PricingRuleNotApplicableException();
		}
		return cost.getForCdr(cdr);
	}

}
