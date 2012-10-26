package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.mongo.ApplicationConstraintsImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Optional;
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
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void simpleWithFutureExpirationAcIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2014"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void futureAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void expiredAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setNullableValidUntilAsDate(DAY_FORMAT.parse("01-05-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentSourceAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("diferentSource"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentCallConnectivityTypeAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.DIY));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void differentCdrTypeAcIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_MOBILE));
		assertResult(false, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleSourcesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability, "source1"));
	}

	@Test
	public void acWithMultipleSourcesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Sets.newHashSet("source1", "source2", "source3", "source4"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(false, invoker.invoke(applicability, "source5"));
	}

	@Test
	public void acWithMultipleConnectivityTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB, CallConnectivityType.DIY));
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithoutConnectivityTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(null);
		applicability.setCdrTypesAsSet(Collections.singleton(Cdr.Type.EXT_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleCdrTypesIsApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Sets.newHashSet(Cdr.Type.EXT_EXT, Cdr.Type.MOBILE_EXT, Cdr.Type.PBX_EXT));
		assertResult(true, invoker.invoke(applicability));
	}

	@Test
	public void acWithMultipleCdrTypesButNotTheRightOneIsNotApplicable() throws Exception {
		applicability.setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
		applicability.setSourcesAsSet(Collections.singleton("any"));
		applicability.setCallConnectivityTypesAsSet(Collections.singleton(CallConnectivityType.OOTB));
		applicability.setCdrTypesAsSet(Sets.newHashSet(Cdr.Type.EXT_MOBILE, Cdr.Type.MOBILE_MOBILE, Cdr.Type.PBX_MOBILE));
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
			return applicability.isApplicable(now, source, CallConnectivityType.OOTB, Cdr.Type.EXT_EXT);
		}
		
	}

	final static class ConnectedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, true, true), CallConnectivityType.OOTB);
		}
		
	}

	final static class ConnectedUntypedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, true, false), CallConnectivityType.OOTB);
		}
		
	}

	final static class UnconnectedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, false, false), CallConnectivityType.OOTB);
		}
		
	}

	final static class UnconnectedUntypedCdrVersionInvoker extends Invoker {

		@Override
		public boolean invoke(ApplicationConstraintsImpl applicability, final String source) {
			return applicability.isApplicable(new StaticCdr(now, source, false, false), CallConnectivityType.OOTB);
		}
		
	}

	final static class StaticCdr implements Cdr {
		
		private final Calendar time;
		
		private final String source;
		
		private final boolean connected;
		
		private final boolean hasCdrType;

		private StaticCdr(Calendar time, String source, boolean connected, boolean hasCdrType) {
			this.time = time;
			this.source = source;
			this.connected = connected;
			this.hasCdrType = hasCdrType;
		}

		@Override
		public String getSource() {
			return source;
		}

		@Override
		public String getCallId() {
			return null;
		}

		@Override
		public Optional<String> getAccount() {
			return Optional.absent();
		}

		@Override
		public Calendar getTime() {
			return time;
		}

		@Override
		public String getFrom() {
			return null;
		}

		@Override
		public String getTo() {
			return null;
		}

		@Override
		public boolean isConnected() {
			return connected;
		}

		@Override
		public Optional<Cdr.Type> getType() {
			return Optional.fromNullable(hasCdrType ? Cdr.Type.EXT_EXT : null);
		}

		@Override
		public long getSeconds() {
			return 0;
		}

		@Override
		public Map<String, String> getAdditionalInfo() {
			return null;
		}
	}

}
