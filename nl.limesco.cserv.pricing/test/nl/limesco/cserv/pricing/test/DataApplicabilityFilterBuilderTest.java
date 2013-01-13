package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.DataApplicabilityFilter;
import nl.limesco.cserv.pricing.mongo.DataApplicabilityFilterBuilderImpl;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DataApplicabilityFilterBuilderTest {

	private DataApplicabilityFilterBuilderImpl builder;
	
	@Before
	public void setUp() {
		builder = new DataApplicabilityFilterBuilderImpl();
	}

	@Test
	public void defaultFilterDoesNotMatchAnything() {
		final DataApplicabilityFilter filter = builder.build();
		assertTrue(filter.getSources().get().isEmpty());
	}

	@Test
	public void anyFilterMatchesAnything() {
		final DataApplicabilityFilter filter = builder
				.source(ApplicabilityFilterBuilder.ANY)
				.build();
		assertFalse(filter.getSources().isPresent());
	}
	
	@Test
	public void sourceFilterMatchesOneSource() {
		final DataApplicabilityFilter filter = builder
				.source("source1")
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
	}

	@Test
	public void twoSourcesFilterMatchesTwoSources() {
		final DataApplicabilityFilter filter = builder
				.source("source1")
				.source("source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
	}

	@Test
	public void twoVarargsSourcesFilterMatchesTwoSources() {
		final DataApplicabilityFilter filter = builder
				.source("source1", "source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
	}

	@Test
	public void twoCollectionSourcesFilterMatchesTwoSources() {
		final DataApplicabilityFilter filter = builder
				.source(Lists.newArrayList("source1", "source2"))
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
	}

}
