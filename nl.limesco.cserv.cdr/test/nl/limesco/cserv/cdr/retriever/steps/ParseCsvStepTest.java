package nl.limesco.cserv.cdr.retriever.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class ParseCsvStepTest {
	
	private final byte[] csv;

	private final String fields;
	
	private final int numberOfCdrs;
	
	private Step step;
	
	public ParseCsvStepTest(String fields, String csv, int numberOfCdrs) {
		this.fields = fields;
		this.csv = csv.getBytes();
		this.numberOfCdrs = numberOfCdrs;
	}
	
	@Before
	public void setUp() throws Exception {
		final Properties properties = new Properties();
		properties.put("source", "testcase");
		properties.put("0_method", "parsecsv");
		properties.put("0_inputvar", "IN");
		properties.put("0_outputvar", "OUT");
		properties.put("0_delimiterchar", ";");
		properties.put("0_quotechar", "\"");
		properties.put("0_skiplines", "0");
		properties.put("0_fields", fields);
		step = Step.newInstance(properties, 0);
	}

	@Test
	public void stepCreatesIterator() throws Exception {
		final Map<String, Object> variables = Maps.newHashMap();
		variables.put("IN", new ByteArrayInputStream(csv));
		assertTrue(step.execute((HttpClient) null, variables));
		assertTrue(variables.get("OUT") instanceof Iterator);
	}

	@Test
	public void canParseCsv() throws Exception {
		final Map<String, Object> variables = Maps.newHashMap();
		variables.put("IN", new ByteArrayInputStream(csv));
		assertTrue(step.execute((HttpClient) null, variables));
		Iterator<Cdr> iterator = (Iterator<Cdr>) variables.get("OUT");
		for (int i = 0; ; i++) {
			if (!iterator.hasNext()) {
				assertEquals(numberOfCdrs, i);
				return;
			}
			final Cdr cdr = iterator.next();
			assertEquals("testcase", cdr.getSource());
			assertEquals(Integer.toString(i), cdr.getCallId());
			assertEquals("acct", cdr.getAccount());
			assertEquals("100", cdr.getFrom());
			assertEquals("101", cdr.getTo());
			final Calendar calendar = Calendar.getInstance();
			final SimpleDateFormat format = new SimpleDateFormat("y-M-d H:m:s");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			calendar.setTime(format.parse("2002-20-" + (i + 1) + " 20:02:20"));
			assertEquals(calendar, cdr.getTime());
			assertEquals(42, cdr.getSeconds());
		}
	}

	@Test
	public void doesNotCreateUnrelatedVariables() throws Exception {
		final Map<String, Object> variables = Maps.newHashMap();
		variables.put("IN", new ByteArrayInputStream(csv));
		assertTrue(step.execute((HttpClient) null, variables));
		assertEquals(1, variables.size());
	}

	@Test
	public void removesInputVariables() throws Exception {
		final Map<String, Object> variables = Maps.newHashMap();
		variables.put("IN", new ByteArrayInputStream(csv));
		assertTrue(step.execute((HttpClient) null, variables));
		assertFalse(variables.containsKey("IN"));
	}
	
	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(
				// Test case with 1 line
				new Object[] { "callId,time y-M-d H:m:s,seconds,account,to,from", "0;2002-20-01 20:02:20;42;acct;101;100", 1 },
				// Test case with ignorable fields
				new Object[] { "-,callId,account,time y-M-d H:m:s,seconds,-,from,to,-,-", "a;0;acct;2002-20-01 20:02:20;42;ignore;100;101;13;37", 1 },
				// Test case with multiple lines, without line separator for last line
				new Object[] { "callId,account,time y-M-d H:m:s,to,from,seconds", "0;acct;2002-20-01 20:02:20;101;100;42\n1;acct;2002-20-02 20:02:20;101;100;42\n2;acct;2002-20-03 20:02:20;101;100;42", 3 },
				// Test case with multiple lines, with line separator for last line
				new Object[] { "callId,account,time y-M-d H:m:s,to,from,seconds", "0;acct;2002-20-01 20:02:20;101;100;42\n1;acct;2002-20-02 20:02:20;101;100;42\n2;acct;2002-20-03 20:02:20;101;100;42\n", 3 });
	}
	
}
