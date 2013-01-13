package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.mongo.SmsApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.SmsPricingImpl;
import nl.limesco.cserv.pricing.mongo.SmsPricingRuleImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SmsPricingRuleTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private final long pricePerSms;
	
	private final long costPerSms;
	
	private SmsPricingRuleImpl rule;
	
	private Calendar now;

	public SmsPricingRuleTest(long pricePerSms, long costPerSms) {
		this.pricePerSms = pricePerSms;
		this.costPerSms = costPerSms;
	}
	
	@Before
	public void setUp() throws Exception {
		final SmsApplicationConstraintsImpl applicability = new SmsApplicationConstraintsImpl();
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("source1"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		
		final SmsPricingImpl price = new SmsPricingImpl();
		price.setPerSms(pricePerSms);
		
		final SmsPricingImpl cost = new SmsPricingImpl();
		cost.setPerSms(costPerSms);

		rule = new SmsPricingRuleImpl();
		rule.setApplicabilityImpl(applicability);
		rule.setPriceImpl(price);
		rule.setCostImpl(cost);

		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		now = Calendar.getInstance();
		now.setTime(sdf.parse("10-10-2010 10:10:10"));
	}

	@Test
	public void cdrHasPricePerSms() throws Exception {
		assertEquals(pricePerSms, rule.getPriceForCdr(new StaticSmsCdr(now, "source1", true)));
	}

	@Test
	public void cdrHasCostPerSms() throws Exception {
		assertEquals(costPerSms, rule.getCostForCdr(new StaticSmsCdr(now, "source1", true)));
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForPrice() throws Exception {
		rule.getPriceForCdr(new StaticSmsCdr(now, "source2", true));
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForCost() throws Exception {
		rule.getCostForCdr(new StaticSmsCdr(now, "source2", true));
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Free SMS
				new Object[] { 0, 0 },
				// Normal SMS
				new Object[] { 710, 700 });
	}
	
}
