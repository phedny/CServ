package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.mongo.ApplicationConstraintsImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Sets;

@RunWith(Parameterized.class)
public class ApplicationConstraintsTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private ApplicationConstraintsImpl applicability;
	
	private final Invoker invoker;
	
	private Boolean expected;
	
	public ApplicationConstraintsTest(Invoker invoker, Boolean expected) {
		this.invoker = invoker;
		this.expected = expected;
	}
	
	@Before
	public void setUp() throws ParseException {
		applicability = new ApplicationConstraintsImpl();
	}

	@Test
	public void simpleAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void simpleWithFutureExpirationAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2014"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void futureAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void expiredAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-05-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentSourceAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("diferentSource"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentCallConnectivityTypeAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.DIY));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentCdrTypeAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_MOBILE));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleSourcesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability, "source1"));
	}

	@Test
	public void acWithMultipleSourcesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability, "source5"));
	}

	@Test
	public void acWithMultipleConnectivityTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB, CallConnectivityType.DIY));
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithoutConnectivityTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(null);
		applicability.setCdrTypesAsSet(Collections.singleton(VoiceCdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleCdrTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Sets.newHashSet(VoiceCdr.Type.EXT_EXT, VoiceCdr.Type.MOBILE_EXT, VoiceCdr.Type.PBX_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleCdrTypesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Sets.newHashSet(VoiceCdr.Type.EXT_MOBILE, VoiceCdr.Type.MOBILE_MOBILE, VoiceCdr.Type.PBX_MOBILE));
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
				new Object[] { new ConnectedCdrVersionInvoker(), null },
				new Object[] { new UnconnectedCdrVersionInvoker(), Boolean.FALSE },
				new Object[] { new ConnectedUntypedCdrVersionInvoker(), Boolean.FALSE },
				new Object[] { new UnconnectedUntypedCdrVersionInvoker(), Boolean.FALSE });
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
		
		boolean invoke(ApplicationConstraintsImpl applicability) {
			return invoke(applicability, "any");
		}
		
		abstract boolean invoke(ApplicationConstraintsImpl applicability, String source);
	}
	
	final static class LongVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, String source) {
			return applicability.isApplicable(now, source, CallConnectivityType.OOTB, VoiceCdr.Type.EXT_EXT);
		}
		
	}

	final static class ConnectedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, true, true, 0), CallConnectivityType.OOTB);
		}
		
	}

	final static class ConnectedUntypedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, true, false, 0), CallConnectivityType.OOTB);
		}
		
	}

	final static class UnconnectedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, false, false, 0), CallConnectivityType.OOTB);
		}
		
	}

	final static class UnconnectedUntypedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, false, false, 0), CallConnectivityType.OOTB);
		}
		
	}

}
