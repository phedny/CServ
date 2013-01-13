package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.pricing.mongo.PricingServiceHelper;
import nl.limesco.cserv.pricing.mongo.PricingServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class PricingServiceGenericsTest<T extends PricingRule> {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};
	
	private final Class<T> clazz;

	private PricingServiceImpl service;
	
	public PricingServiceGenericsTest(Class<T> clazz) {
		this.clazz = clazz;
	}
	
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
	public void hasPricingRulesForDay() throws Exception {
		assertEquals(2, service.getApplicablePricingRules(clazz, calendar("01-05-2011")).size());
	}
	
	@Test
	public void hasNoPricingRulesALongLongTimeAgo() throws Exception {
		assertEquals(0, service.getApplicablePricingRules(clazz, calendar("03-03-1847")).size());
	}
	
	@Test
	public void hasPricingRulesInTheFuture() throws Exception {
		assertEquals(1, service.getApplicablePricingRules(clazz, calendar("01-01-2044")).size());
	}

	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Voice related pricing
				new Object[] { VoicePricingRule.class },
				// SMS related pricing
				new Object[] { SmsPricingRule.class },
				// Data related pricing
				new Object[] { DataPricingRule.class });
	}
	
}
