package nl.limesco.cserv.cdr.retriever.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class FormatDateStepTest {

	private Step step;
	
	private Calendar calendar;
	
	private Map<String, Object> variables;
	
	@Before
	public void setUp() throws Exception {
		final Properties properties = new Properties();
		properties.put("0_method", "formatdate");
		properties.put("0_inputvar", "IN");
		properties.put("0_outputvar", "OUT");
		properties.put("0_pattern", "yyyy-MM-dd HH:mm:ss");
		step = Step.newInstance(properties, 0);
		
		variables = Maps.newHashMap();
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(1234567890000l);
		variables.put("IN", calendar);
	}
	
	@Test
	public void canFormatDate() throws Exception {
		assertTrue(step.execute((HttpClient) null, variables));
		assertEquals("2009-02-14 00:31:30", variables.get("OUT"));
	}

	@Test
	public void doesNotCreateUnrelatedVariables() throws Exception {
		assertTrue(step.execute((HttpClient) null, variables));
		assertEquals(2, variables.size());
		assertTrue(variables.containsKey("IN"));
	}

	@Test
	public void doesNotModifyInputVariables() throws Exception {
		assertTrue(step.execute((HttpClient) null, variables));
		assertEquals(calendar, variables.get("IN"));
	}
	
}
