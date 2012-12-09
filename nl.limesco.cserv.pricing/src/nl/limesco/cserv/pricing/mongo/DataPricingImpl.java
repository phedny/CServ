package nl.limesco.cserv.pricing.mongo;

import java.math.BigDecimal;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.pricing.api.DataPricing;

public class DataPricingImpl implements DataPricing {

	private long perKilobyte;
	
	@Override
	public long getPerKilobyte() {
		return perKilobyte;
	}
	
	public void setPerKilobyte(long perKilobyte) {
		this.perKilobyte = perKilobyte;
	}

	@Override
	public long getForCdr(Cdr cdr) {
		final BigDecimal numberOfKilobytesBD = BigDecimal.valueOf(((DataCdr) cdr).getKilobytes());
		final BigDecimal pricePerKilobyteBD = BigDecimal.valueOf(perKilobyte);
		return numberOfKilobytesBD.multiply(pricePerKilobyteBD).longValue();
	}

}
