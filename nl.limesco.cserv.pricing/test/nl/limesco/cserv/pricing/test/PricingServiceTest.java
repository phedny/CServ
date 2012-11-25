package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.mongo.PricingServiceHelper;
import nl.limesco.cserv.pricing.mongo.PricingServiceImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;

public class PricingServiceTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private PricingServiceHelper helper;
	
	private PricingServiceImpl service;
	
	@Before
	public void setUp() {
		helper = new PricingServiceHelperMock();
		service = new PricingServiceImpl(helper);
	}
	
	@Test
	public void hasPricingRulesForDay() throws Exception {
		assertEquals(2, service.getApplicablePricingRules(calendar("01-05-2011")).size());
	}
	
	@Test
	public void hasNoPricingRulesALongLongTimeAgo() throws Exception {
		assertEquals(0, service.getApplicablePricingRules(calendar("03-03-1847")).size());
	}
	
	@Test
	public void hasPricingRulesInTheFuture() throws Exception {
		assertEquals(1, service.getApplicablePricingRules(calendar("01-01-2044")).size());
	}

	@Test
	public void hasPricingRulesForExistingSourceCdr() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(2, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdr() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void hasPricingRulesForExistingSourceCdrAndCallConnectivityType() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(2, service.getApplicablePricingRules(cdr, CallConnectivityType.OOTB).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdrAndCallConnectivityType() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.OOTB).size());
	}

	@Test
	public void hasPricingRulesForExistingSourceCdrAndWrongCallConnectivityType() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.DIY).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdrAndWrongCallConnectivityType() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.DIY).size());
	}

	@Test
	public void cdrHasPrice() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		assertEquals(1110, service.getApplicablePrice(cdr, CallConnectivityType.OOTB));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoPrice() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source42", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.OOTB);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForWrongCallConnectivityTypeHasNoPrice() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.DIY);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoPrice() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.OOTB);
	}

	@Test
	public void cdrHasCost() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		assertEquals(1000, service.getApplicableCost(cdr, CallConnectivityType.OOTB));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoCost() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source42", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.OOTB);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForWrongCallConnectivityTypeHasNoCost() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.DIY);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoCost() throws Exception {
		final StaticCdr cdr = new StaticCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.OOTB);
	}
	
	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

}
