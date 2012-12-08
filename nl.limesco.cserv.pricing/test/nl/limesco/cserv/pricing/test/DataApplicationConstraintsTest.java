package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.pricing.mongo.DataApplicationConstraintsImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Sets;

@RunWith(Parameterized.class)
public class DataApplicationConstraintsTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private DataApplicationConstraintsImpl applicability;
	
	private final Invoker invoker;
	
	private Boolean expected;
	
	public DataApplicationConstraintsTest(Invoker invoker, Boolean expected) {
		this.invoker = invoker;
		this.expected = expected;
	}
	
	@Before
	public void setUp() throws ParseException {
		applicability = new DataApplicationConstraintsImpl();
	}

	@Test
	public void simpleAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void simpleWithFutureExpirationAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2014"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void futureAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void expiredAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-05-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentSourceAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("diferentSource"));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleSourcesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		assertResult(true, invoker.invoke(applicability, "source1"));
	}

	@Test
	public void acWithMultipleSourcesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		assertResult(false, invoker.invoke(applicability, "source5"));
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
				new Object[] { new CdrVersionInvoker(), null });
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
		
		boolean invoke(DataApplicationConstraintsImpl applicability) {
			return invoke(applicability, "any");
		}
		
		abstract boolean invoke(DataApplicationConstraintsImpl applicability, String source);
	}
	
	final static class LongVersionInvoker extends Invoker {

		@Override
		public boolean invoke(DataApplicationConstraintsImpl applicability, String source) {
			return applicability.isApplicable(now, source);
		}
		
	}

	final static class CdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(DataApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticDataCdr(now, source, 1));
		}
		
	}

}
