package nl.limesco.cserv.invoice.mongo;

import java.math.BigDecimal;

import nl.limesco.cserv.invoice.api.DurationItemLine;

public class DurationItemLineImpl extends AbstractItemLine implements DurationItemLine {
	
	private static final BigDecimal SECONDS_PER_MINUTE_BD = BigDecimal.valueOf(60);

	private long pricePerCall;
	
	private long pricePerMinute;
	
	private long numberOfCalls;
	
	private long numberOfSeconds;

	@Override
	public long getPricePerCall() {
		return pricePerCall;
	}

	public void setPricePerCall(long pricePerCall) {
		this.pricePerCall = pricePerCall;
	}

	@Override
	public long getPricePerMinute() {
		return pricePerMinute;
	}

	public void setPricePerMinute(long pricePerMinute) {
		this.pricePerMinute = pricePerMinute;
	}

	@Override
	public long getNumberOfCalls() {
		return numberOfCalls;
	}

	public void setNumberOfCalls(long numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	@Override
	public long getNumberOfSeconds() {
		return numberOfSeconds;
	}

	public void setNumberOfSeconds(long numberOfSeconds) {
		this.numberOfSeconds = numberOfSeconds;
	}

	@Override
	protected long computeTotalPrice() {
		final long totalPpcPrice = numberOfCalls * pricePerCall;
		
		final BigDecimal numberOfSecondsBD = BigDecimal.valueOf(numberOfSeconds);
		final BigDecimal pricePerMinuteBD = BigDecimal.valueOf(pricePerMinute);
		final long totalPpmPrice = numberOfSecondsBD.multiply(pricePerMinuteBD).divideToIntegralValue(SECONDS_PER_MINUTE_BD).longValue();
		
		return totalPpcPrice + totalPpmPrice;
	}
	
}
