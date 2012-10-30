package nl.limesco.cserv.pricing.mongo;

import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.ApplicationConstraints;
import nl.limesco.cserv.pricing.api.Pricing;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class PricingRuleImpl implements PricingRule {
	
	private String id;
	
	private String description;
	
	private ApplicationConstraintsImpl applicability;
	
	private PricingImpl price;
	
	private PricingImpl cost;

	@ObjectId
	@JsonProperty("_id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	@JsonIgnore
	public ApplicationConstraints getApplicability() {
		return applicability;
	}
	
	@JsonProperty("applicability")
	public ApplicationConstraintsImpl getApplicabilityImpl() {
		return applicability;
	}
	
	public void setApplicabilityImpl(ApplicationConstraintsImpl applicability) {
		this.applicability = applicability;
	}

	@Override
	@JsonIgnore
	public Pricing getPrice() {
		return price;
	}
	
	@JsonProperty("price")
	public PricingImpl getPriceImpl() {
		return price;
	}
	
	public void setPriceImpl(PricingImpl price) {
		this.price = price;
	}

	@Override
	@JsonIgnore
	public Pricing getCost() {
		return cost;
	}
	
	@JsonProperty("cost")
	public PricingImpl getCostImpl() {
		return cost;
	}
	
	public void setCostImpl(PricingImpl cost) {
		this.cost = cost;
	}

	@Override
	public long getPriceForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException {
		if (!cdr.isConnected()) {
			return 0;
		}
		if (!applicability.isApplicable(cdr, callConnectivityType)) {
			throw new PricingRuleNotApplicableException();
		}
		return price.getForCdr(cdr);
	}

	@Override
	public long getCostForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException {
		if (!cdr.isConnected()) {
			return 0;
		}
		if (!applicability.isApplicable(cdr, callConnectivityType)) {
			throw new PricingRuleNotApplicableException();
		}
		return cost.getForCdr(cdr);
	}

}
