package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.mongo.SmsApplicationConstraintsImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Sets;

@RunWith(Parameterized.class)
public class SmsApplicationConstraintsTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private SmsApplicationConstraintsImpl applicability;
	
	private final Invoker invoker;
	
	private Boolean expected;
	
	public SmsApplicationConstraintsTest(Invoker invoker, Boolean expected) {
		this.invoker = invoker;
		this.expected = expected;
	}
	
	@Before
	public void setUp() throws ParseException {
		applicability = new SmsApplicationConstraintsImpl();
	}

	@Test
	public void simpleAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void simpleWithFutureExpirationAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2014"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void futureAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void expiredAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-05-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentSourceAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("diferentSource"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentCdrTypeAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.EXT_MOBILE));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleSourcesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(true, invoker.invoke(applicability, "source1"));
	}

	@Test
	public void acWithMultipleSourcesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCdrTypesAsSet(Collections.singleton(SmsCdr.Type.MOBILE_EXT));
		assertResult(false, invoker.invoke(applicability, "source5"));
	}

	@Test
	public void acWithMultipleCdrTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Sets.newHashSet(SmsCdr.Type.MOBILE_EXT, SmsCdr.Type.EXT_MOBILE));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleCdrTypesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCdrTypesAsSet(Sets.newHashSet(SmsCdr.Type.EXT_MOBILE, SmsCdr.Type.EXT_MOBILE));
		assertResult(false, invoker.invoke(applicability));
	}
	
	private void assertResult(boolean expected, boolean actual) {
		if (this.expected != null) {
			assertEquals(this.expected.booleanValue(), actual);
		} else {
			assertEquals(expected, actual);
		}
	}

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				new Object[] { new LongVersionInvoker(), null },
				new Object[] { new CdrVersionInvoker(), null },
				new Object[] { new UntypedCdrVersionInvoker(), Boolean.FALSE });
	}
	
	abstract static class Invoker {
		protected Calendar now = Calendar.getInstance();
		
		public Invoker() {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			try {
				now.setTime(sdf.parse("10-10-2010 10:10:10"));
			} catch (ParseException e) {
			}
		}
		
		boolean invoke(SmsApplicationConstraintsImpl applicability) {
			return invoke(applicability, "any");
		}
		
		abstract boolean invoke(SmsApplicationConstraintsImpl applicability, String source);
	}
	
	final static class LongVersionInvoker extends Invoker {

		@Override
		public boolean invoke(SmsApplicationConstraintsImpl applicability, String source) {
			return applicability.isApplicable(now, source, SmsCdr.Type.MOBILE_EXT);
		}
		
	}

	final static class CdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(SmsApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticSmsCdr(now, source, true));
		}
		
	}

	final static class UntypedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(SmsApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticSmsCdr(now, source, false));
		}
		
	}

}
