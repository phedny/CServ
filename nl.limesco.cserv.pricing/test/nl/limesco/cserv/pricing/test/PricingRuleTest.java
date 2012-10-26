package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.mongo.ApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.PricingImpl;
import nl.limesco.cserv.pricing.mongo.PricingRuleImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PricingRuleTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private final long pricePerCall;
	
	private final long pricePerMinute;
	
	private final long costPerCall;
	
	private final long costPerMinute;
	
	private PricingRuleImpl rule;
	
	private Calendar now;

	public PricingRuleTest(long pricePerCall, long pricePerMinute, long costPerCall, long costPerMinute) {
		this.pricePerCall = pricePerCall;
		this.pricePerMinute = pricePerMinute;
		this.costPerCall = costPerCall;
		this.costPerMinute = costPerMinute;
	}
	
	@Before
	public void setUp() throws Exception {
		final ApplicationConstraintsImpl applicability = new ApplicationConstraintsImpl();
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("source1"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		
		final PricingImpl price = new PricingImpl();
		price.setPerCall(pricePerCall);
		price.setPerMinute(pricePerMinute);
		
		final PricingImpl cost = new PricingImpl();
		cost.setPerCall(costPerCall);
		cost.setPerMinute(costPerMinute);

		rule = new PricingRuleImpl();
		rule.setApplicabilityImpl(applicability);
		rule.setPriceImpl(price);
		rule.setCostImpl(cost);

		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		now = Calendar.getInstance();
		now.setTime(sdf.parse("10-10-2010 10:10:10"));
	}

	@Test
	public void zeroSecondCdrHasOnlyPricePerCall() throws Exception {
		assertEquals(pricePerCall, rule.getPriceForCdr(new StaticCdr(now, "source1", true, true, 0), CallConnectivityType.OOTB));
	}

	@Test
	public void zeroSecondCdrHasOnlyCostPerCall() throws Exception {
		assertEquals(costPerCall, rule.getCostForCdr(new StaticCdr(now, "source1", true, true, 0), CallConnectivityType.OOTB));
	}

	@Test
	public void oneMinuteCdrHasPricePerCallPlusPricePerMinute() throws Exception {
		assertEquals(pricePerCall + pricePerMinute, rule.getPriceForCdr(new StaticCdr(now, "source1", true, true, 60), CallConnectivityType.OOTB));
	}

	@Test
	public void oneMinuteCdrHasCostPerCallPlusCostPerMinute() throws Exception {
		assertEquals(costPerCall + costPerMinute, rule.getCostForCdr(new StaticCdr(now, "source1", true, true, 60), CallConnectivityType.OOTB));
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForPrice() throws Exception {
		rule.getPriceForCdr(new StaticCdr(now, "source2", true, true, 60), CallConnectivityType.OOTB);
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForCost() throws Exception {
		rule.getCostForCdr(new StaticCdr(now, "source2", true, true, 60), CallConnectivityType.OOTB);
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Free calls
				new Object[] { 0, 0, 0, 0 },
				// Calls without ppm
				new Object[] { 400, 0, 300, 0 },
				// Calls without ppc
				new Object[] { 0, 710, 0, 700 },
				// Normal calls
				new Object[] { 400, 710, 300, 700 });
	}
	
}
