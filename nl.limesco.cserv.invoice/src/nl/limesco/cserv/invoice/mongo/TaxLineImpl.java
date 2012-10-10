package nl.limesco.cserv.invoice.mongo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nl.limesco.cserv.invoice.api.TaxLine;

import org.codehaus.jackson.annotate.JsonIgnore;

public class TaxLineImpl implements TaxLine {

	private long baseAmount;
	
	private long taxAmount;
	
	double taxRate;

	@Override
	public long getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(long baseAmount) {
		this.baseAmount = baseAmount;
	}

	@Override
	public long getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(long taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	public void setTaxAmount() {
		taxAmount = computeTaxAmount();
	}

	@Override
	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}
	
	public long computeTaxAmount() {
		final BigDecimal baseAmountBD = BigDecimal.valueOf(baseAmount);
		final BigDecimal taxRateBD = BigDecimal.valueOf(taxRate);
		return baseAmountBD.multiply(taxRateBD).setScale(0, RoundingMode.HALF_UP).longValue();
	}
	
	@JsonIgnore
	public boolean isSound() {
		return computeTaxAmount() == taxAmount;
	}
	
}
