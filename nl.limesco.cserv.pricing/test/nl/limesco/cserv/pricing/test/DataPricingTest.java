package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import nl.limesco.cserv.pricing.mongo.DataPricingImpl;

import org.junit.Before;
import org.junit.Test;

public class DataPricingTest {
  
	private DataPricingImpl pricing;
	
	@Before
	public void setUp() {
		pricing = new DataPricingImpl();
		pricing.setPerKilobyte(1250);
	}
	
	@Test
	public void cdrHasPricePerKilobyte() {
		assertEquals(1250, pricing.getForCdr(new StaticDataCdr(null, null, 1)));
	}
	
}
