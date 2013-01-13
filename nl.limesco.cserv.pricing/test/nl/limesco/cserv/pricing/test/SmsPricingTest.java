package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import nl.limesco.cserv.pricing.mongo.SmsPricingImpl;

import org.junit.Before;
import org.junit.Test;

public class SmsPricingTest {
  
	private SmsPricingImpl pricing;
	
	@Before
	public void setUp() {
		pricing = new SmsPricingImpl();
		pricing.setPerSms(1250);
	}
	
	@Test
	public void cdrHasPricePerSms() {
		assertEquals(1250, pricing.getForCdr(new StaticSmsCdr(null, null, false)));
	}
	
}
