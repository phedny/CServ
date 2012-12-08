package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilter;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.mongo.SmsApplicabilityFilterBuilderImpl;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class SmsApplicabilityFilterBuilderTest {

	private SmsApplicabilityFilterBuilderImpl builder;
	
	@Before
	public void setUp() {
		builder = new SmsApplicabilityFilterBuilderImpl();
	}

	@Test
	public void defaultFilterDoesNotMatchAnything() {
		final SmsApplicabilityFilter filter = builder.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void anyFilterMatchesAnything() {
		final SmsApplicabilityFilter filter = builder
				.source(SmsApplicabilityFilterBuilder.ANY)
				.cdrType(SmsApplicabilityFilterBuilder.ANY)
				.build();
		assertFalse(filter.getSources().isPresent());
		assertFalse(filter.getCdrTypes().isPresent());
	}
	
	@Test
	public void sourceFilterMatchesOneSource() {
		final SmsApplicabilityFilter filter = builder
				.source("source1")
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoSourcesFilterMatchesTwoSources() {
		final SmsApplicabilityFilter filter = builder
				.source("source1")
				.source("source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoVarargsSourcesFilterMatchesTwoSources() {
		final SmsApplicabilityFilter filter = builder
				.source("source1", "source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoCollectionSourcesFilterMatchesTwoSources() {
		final SmsApplicabilityFilter filter = builder
				.source(Lists.newArrayList("source1", "source2"))
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void cdrTypeFilterMatchesOneCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdrType(SmsCdr.Type.MOBILE_EXT)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(SmsCdr.Type.MOBILE_EXT, filter.getCdrTypes().get().iterator().next());
	}

	@Test
	public void twoCdrTypesFilterMatchesTwoCdrTypes() {
		final SmsApplicabilityFilter filter = builder
				.cdrType(SmsCdr.Type.MOBILE_EXT)
				.cdrType(SmsCdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<SmsCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(set.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoVarargsCdrTypesFilterMatchesTwoCdrTypes() {
		final SmsApplicabilityFilter filter = builder
				.cdrType(SmsCdr.Type.MOBILE_EXT, SmsCdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<SmsCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(set.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoCollectionCdrTypesFilterMatchesTwoCdrTypes() {
		final SmsApplicabilityFilter filter = builder
				.cdrType(Lists.newArrayList(SmsCdr.Type.MOBILE_EXT, SmsCdr.Type.EXT_MOBILE))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<SmsCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(set.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void cdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(new StaticSmsCdr(null, "source1", true))
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(SmsCdr.Type.MOBILE_EXT, filter.getCdrTypes().get().iterator().next());
	}

	@Test
	public void twoDisjointCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT))
				.cdr(new StaticSmsCdr(null, "source2", SmsCdr.Type.EXT_MOBILE))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoDisjointVarargsCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT),
						new StaticSmsCdr(null, "source2", SmsCdr.Type.EXT_MOBILE))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoDisjointCollectionCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT),
						(Cdr) new StaticSmsCdr(null, "source2", SmsCdr.Type.EXT_MOBILE)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoOverlappingCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT))
				.cdr(new StaticSmsCdr(null, "source2", SmsCdr.Type.MOBILE_EXT))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
	}

	@Test
	public void twoOverlappingVarargsCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT),
						new StaticSmsCdr(null, "source2", SmsCdr.Type.MOBILE_EXT))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
	}

	@Test
	public void twoOverlappingCollectionCdrFilterMatchesSourceAndCdrType() {
		final SmsApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticSmsCdr(null, "source1", SmsCdr.Type.MOBILE_EXT),
						(Cdr) new StaticSmsCdr(null, "source2", SmsCdr.Type.MOBILE_EXT)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		final Collection<SmsCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(SmsCdr.Type.MOBILE_EXT));
	}
	
}
