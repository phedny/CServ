package nl.limesco.cserv.pricing.mongo;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.pricing.api.PricingRule;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "service")
@JsonSubTypes({
	@JsonSubTypes.Type(value = VoicePricingRuleImpl.class, name = "voice")
})
public abstract class AbstractPricingRuleImpl implements PricingRule {

	private String id;
	
	private String description;
	
	private boolean hidden;

	public AbstractPricingRuleImpl() {
		super();
	}

	@ObjectId
	@Id
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
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}