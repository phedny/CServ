package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.pricing.mongo.PricingServiceHelper;
import nl.limesco.cserv.pricing.mongo.PricingServiceImpl;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class PricingServiceDataTest {

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
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2011"), "source1", 1024);
		assertEquals(2, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void hasNoPricingRulesForNonExistentSourceCdr() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2011"), "source42", 1024);
		assertEquals(0, service.getApplicablePricingRules(cdr).size());
	}

	@Test
	public void cdrHasPrice() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2010"), "source1", 1024);
		assertEquals(1024 * 710, service.getApplicablePrice(cdr));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoPrice() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2010"), "source42", 1024);
		service.getApplicablePrice(cdr);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoPrice() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2011"), "source1", 1024);
		service.getApplicablePrice(cdr);
	}

	@Test
	public void cdrHasCost() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2010"), "source1", 1024);
		assertEquals(1024 * 700, service.getApplicableCost(cdr));
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrForNonExistentSourceHasNoCost() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2010"), "source42", 1024);
		service.getApplicableCost(cdr);
	}

	@Test(expected = NoApplicablePricingRuleException.class)
	public void cdrWithMultipleMatchingPricingRulesHasNoCost() throws Exception {
		final StaticDataCdr cdr = new StaticDataCdr(calendar("01-05-2011"), "source1", 1024);
		service.getApplicableCost(cdr);
	}
	
	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

}
