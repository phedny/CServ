package nl.limesco.cserv.pricing.mongo;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.SmsPricing;

public class SmsPricingImpl implements SmsPricing {

	private long perSms;
	
	@Override
	public long getPerSms() {
		return perSms;
	}
	
	public void setPerSms(long perSms) {
		this.perSms = perSms;
	}

	@Override
	public long getForCdr(Cdr cdr) {
		return perSms;
	}

}
