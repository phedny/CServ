package nl.limesco.cserv.pricing.mongo;

import java.math.BigDecimal;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.Pricing;

public class PricingImpl implements Pricing {
	
	private static final BigDecimal SECONDS_PER_MINUTE_BD = BigDecimal.valueOf(60);

	private long perCall;
	
	private long perMinute;
	
	@Override
	public long getPerCall() {
		return perCall;
	}
	
	public void setPerCall(long perCall) {
		this.perCall = perCall;
	}

	@Override
	public long getPerMinute() {
		return perMinute;
	}
	
	public void setPerMinute(long perMinute) {
		this.perMinute = perMinute;
	}

	@Override
	public long getForCdr(Cdr cdr) {
		final long totalPpcPrice = ((VoiceCdr) cdr).isConnected() ? perCall : 0;
		
		final BigDecimal numberOfSecondsBD = BigDecimal.valueOf(((VoiceCdr) cdr).getSeconds());
		final BigDecimal pricePerMinuteBD = BigDecimal.valueOf(perMinute);
		final long totalPpmPrice = numberOfSecondsBD.multiply(pricePerMinuteBD).divideToIntegralValue(SECONDS_PER_MINUTE_BD).longValue();
		
		return totalPpcPrice + totalPpmPrice;
	}

}
