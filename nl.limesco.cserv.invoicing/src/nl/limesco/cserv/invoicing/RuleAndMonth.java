package nl.limesco.cserv.invoicing;

import com.google.common.base.Objects;

public class RuleAndMonth {

	private final String rule;
	
	private final int month;
	
	private final boolean inContract;

	public RuleAndMonth(int month, String rule, boolean inContract) {
		this.month = month;
		this.rule = rule;
		this.inContract = inContract;
	}
	
	public RuleAndMonth(int year, int month, String rule, boolean inContract) {
		this(12 * year + month, rule, inContract);
	}

	public String getRule() {
		return rule;
	}

	public int getMonth() {
		return month;
	}

	public boolean isInContract() {
		return inContract;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(rule, month, inContract);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		RuleAndMonth other = (RuleAndMonth) obj;
		return Objects.equal(rule, other.rule)
				&& Objects.equal(month, other.month)
				&& Objects.equal(inContract, other.inContract);
	}
	
}
