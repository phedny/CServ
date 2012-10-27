package nl.limesco.cserv.cdr.mongo;

import nl.limesco.cserv.cdr.api.Cdr;

public class CdrPricingImpl implements Cdr.Pricing {

	private String pricingRuleId;
	
	private long computedPrice;
	
	private long computedCost;
	
	@Override
	public String getPricingRuleId() {
		return pricingRuleId;
	}
	
	public void setPricingRuleId(String pricingRuleId) {
		this.pricingRuleId = pricingRuleId;
	}

	@Override
	public long getComputedPrice() {
		return computedPrice;
	}
	
	public void setComputedPrice(long computedPrice) {
		this.computedPrice = computedPrice;
	}

	@Override
	public long getComputedCost() {
		return computedCost;
	}
	
	public void setComputedCost(long computedCost) {
		this.computedCost = computedCost;
	}

}
