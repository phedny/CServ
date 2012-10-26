package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.mongo.PricingImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PricingTest {
  
	private final long pricePerCall;
	
	private final long pricePerMinute;
	
	private PricingImpl pricing;
	
	public PricingTest(long pricePerCall, long pricePerMinute) {
		this.pricePerCall = pricePerCall;
		this.pricePerMinute = pricePerMinute;
	}
	
	@Before
	public void setUp() {
		pricing = new PricingImpl();
		pricing.setPerCall(pricePerCall);
		pricing.setPerMinute(pricePerMinute);
	}
	
	@Test
	public void unconnectedCdrHasZeroPrice() {
		assertEquals(0, pricing.getForCdr(new StaticCdr(null, null, false, false, 0)));
	}
	
	@Test
	public void zeroSecondCdrHasOnlyPricePerCall() {
		assertEquals(pricePerCall, pricing.getForCdr(new StaticCdr(null, null, true, false, 0)));
	}
	
	@Test
	public void oneMinuteCdrHasPricePerCallPlusPricePerMinute() {
		assertEquals(pricePerCall + pricePerMinute, pricing.getForCdr(new StaticCdr(null, null, true, false, 60)));
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Free calls
				new Object[] { 0, 0 },
				// Calls without ppm
				new Object[] { 400, 0 },
				// Calls without ppc
				new Object[] { 0, 710 },
				// Normal calls
				new Object[] { 400, 710 });
	}
	
}
