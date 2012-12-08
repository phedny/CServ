package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.pricing.mongo.PricingServiceHelper;
import nl.limesco.cserv.pricing.mongo.PricingServiceImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class PricingServiceVoiceTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private PricingServiceImpl service;
	
	@Before
	@SuppressWarnings("rawtypes")
	public void setUp() {
		final Map<Class, PricingServiceHelper> helpers = Maps.newHashMap();
		helpers.put(VoicePricingRule.class, new VoicePricingServiceHelperMock());
		helpers.put(SmsPricingRule.class, new SmsPricingServiceHelperMock());
		helpers.put(DataPricingRule.class, new DataPricingServiceHelperMock());
		service = new PricingServiceImpl(helpers);
	}
	
	@Test
	public void hasPricingRulesForExistingSourceCdr() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(2, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdr() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void hasPricingRulesForExistingSourceCdrAndCallConnectivityType() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(2, service.getApplicablePricingRules(cdr, CallConnectivityType.OOTB).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdrAndCallConnectivityType() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.OOTB).size());
	}

	@Test
	public void hasPricingRulesForExistingSourceCdrAndWrongCallConnectivityType() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.DIY).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdrAndWrongCallConnectivityType() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source42", true, VoiceCdr.Type.EXT_EXT, 0);
		assertEquals(0, service.getApplicablePricingRules(cdr, CallConnectivityType.DIY).size());
	}

	@Test
	public void cdrHasPrice() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		assertEquals(1110, service.getApplicablePrice(cdr, CallConnectivityType.OOTB));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoPrice() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source42", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.OOTB);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForWrongCallConnectivityTypeHasNoPrice() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.DIY);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoPrice() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicablePrice(cdr, CallConnectivityType.OOTB);
	}

	@Test
	public void cdrHasCost() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		assertEquals(1000, service.getApplicableCost(cdr, CallConnectivityType.OOTB));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoCost() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source42", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.OOTB);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForWrongCallConnectivityTypeHasNoCost() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2010"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.DIY);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoCost() throws Exception {
		final StaticVoiceCdr cdr = new StaticVoiceCdr(calendar("01-05-2011"), "source1", true, VoiceCdr.Type.EXT_EXT, 60);
		service.getApplicableCost(cdr, CallConnectivityType.OOTB);
	}
	
	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

}
