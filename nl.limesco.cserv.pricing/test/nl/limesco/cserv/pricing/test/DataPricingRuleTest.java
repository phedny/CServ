package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.mongo.DataApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.DataPricingImpl;
import nl.limesco.cserv.pricing.mongo.DataPricingRuleImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DataPricingRuleTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private final long pricePerKilobyte;
	
	private final long costPerKilobyte;
	
	private DataPricingRuleImpl rule;
	
	private Calendar now;

	public DataPricingRuleTest(long pricePerKilobyte, long costPerKilobyte) {
		this.pricePerKilobyte = pricePerKilobyte;
		this.costPerKilobyte = costPerKilobyte;
	}
	
	@Before
	public void setUp() throws Exception {
		final DataApplicationConstraintsImpl applicability = new DataApplicationConstraintsImpl();
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("source1"));
		
		final DataPricingImpl price = new DataPricingImpl();
		price.setPerKilobyte(pricePerKilobyte);
		
		final DataPricingImpl cost = new DataPricingImpl();
		cost.setPerKilobyte(costPerKilobyte);

		rule = new DataPricingRuleImpl();
		rule.setApplicabilityImpl(applicability);
		rule.setPriceImpl(price);
		rule.setCostImpl(cost);

		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		now = Calendar.getInstance();
		now.setTime(sdf.parse("10-10-2010 10:10:10"));
	}

	@Test
	public void cdrHasPricePerKilobyte() throws Exception {
		assertEquals(pricePerKilobyte, rule.getPriceForCdr(new StaticDataCdr(now, "source1", 1)));
	}

	@Test
	public void cdrHasCostPerKilobyte() throws Exception {
		assertEquals(costPerKilobyte, rule.getCostForCdr(new StaticDataCdr(now, "source1", 1)));
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForPrice() throws Exception {
		rule.getPriceForCdr(new StaticDataCdr(now, "source2", 1));
	}

	@Test(expected = PricingRuleNotApplicableException.class)
	public void notApplicableCdrThrowsExceptionWhenAskingForCost() throws Exception {
		rule.getCostForCdr(new StaticDataCdr(now, "source2", 1));
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Free Data
				new Object[] { 0, 0 },
				// Normal Data
				new Object[] { 710, 700 });
	}
	
}
