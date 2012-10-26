package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.mongo.ApplicabilityFilterBuilderImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ApplicabilityFilterBuilderTest {

	private ApplicabilityFilterBuilderImpl builder;
	
	@Before
	public void setUp() {
		builder = new ApplicabilityFilterBuilderImpl();
	}

	@Test
	public void defaultFilterDoesNotMatchAnything() {
		final ApplicabilityFilter filter = builder.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void anyFilterMatchesAnything() {
		final ApplicabilityFilter filter = builder
				.source(ApplicabilityFilterBuilder.ANY)
				.callConnectivityType(ApplicabilityFilterBuilder.ANY)
				.cdrType(ApplicabilityFilterBuilder.ANY)
				.build();
		assertFalse(filter.getSources().isPresent());
		assertFalse(filter.getCallConnectivityTypes().isPresent());
		assertFalse(filter.getCdrTypes().isPresent());
	}
	
	@Test
	public void sourceFilterMatchesOneSource() {
		final ApplicabilityFilter filter = builder
				.source("source1")
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoSourcesFilterMatchesTwoSources() {
		final ApplicabilityFilter filter = builder
				.source("source1")
				.source("source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoVarargsSourcesFilterMatchesTwoSources() {
		final ApplicabilityFilter filter = builder
				.source("source1", "source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoCollectionSourcesFilterMatchesTwoSources() {
		final ApplicabilityFilter filter = builder
				.source(Lists.newArrayList("source1", "source2"))
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void callConnectivityTypeFilterMatchesOneCallConnectivityType() {
		final ApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertEquals(1, filter.getCallConnectivityTypes().get().size());
		assertEquals(CallConnectivityType.OOTB, filter.getCallConnectivityTypes().get().iterator().next());
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final ApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB)
				.callConnectivityType(CallConnectivityType.DIY)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoVarargsCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final ApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB, CallConnectivityType.DIY)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void twoCollectionCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final ApplicabilityFilter filter = builder
				.callConnectivityType(Lists.newArrayList(CallConnectivityType.OOTB, CallConnectivityType.DIY))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
	}

	@Test
	public void cdrTypeFilterMatchesOneCdrType() {
		final ApplicabilityFilter filter = builder
				.cdrType(Cdr.Type.EXT_EXT)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(Cdr.Type.EXT_EXT, filter.getCdrTypes().get().iterator().next());
	}

	@Test
	public void twoCdrTypesFilterMatchesTwoCdrTypes() {
		final ApplicabilityFilter filter = builder
				.cdrType(Cdr.Type.EXT_EXT)
				.cdrType(Cdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(Cdr.Type.EXT_EXT));
		assertTrue(set.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoVarargsCdrTypesFilterMatchesTwoCdrTypes() {
		final ApplicabilityFilter filter = builder
				.cdrType(Cdr.Type.EXT_EXT, Cdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(Cdr.Type.EXT_EXT));
		assertTrue(set.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoCollectionCdrTypesFilterMatchesTwoCdrTypes() {
		final ApplicabilityFilter filter = builder
				.cdrType(Lists.newArrayList(Cdr.Type.EXT_EXT, Cdr.Type.EXT_MOBILE))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(Cdr.Type.EXT_EXT));
		assertTrue(set.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void cdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(new StaticCdr(null, "source1", true, true, 0))
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(Cdr.Type.EXT_EXT, filter.getCdrTypes().get().iterator().next());
	}

	@Test
	public void twoDisjointCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0))
				.cdr(new StaticCdr(null, "source2", true, Cdr.Type.EXT_MOBILE, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoDisjointVarargsCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0),
						new StaticCdr(null, "source2", true, Cdr.Type.EXT_MOBILE, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoDisjointCollectionCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0),
						(Cdr) new StaticCdr(null, "source2", true, Cdr.Type.EXT_MOBILE, 0)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_MOBILE));
	}

	@Test
	public void twoOverlappingCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0))
				.cdr(new StaticCdr(null, "source2", true, Cdr.Type.EXT_EXT, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
	}

	@Test
	public void twoOverlappingVarargsCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0),
						new StaticCdr(null, "source2", true, Cdr.Type.EXT_EXT, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
	}

	@Test
	public void twoOverlappingCollectionCdrFilterMatchesSourceAndCdrType() {
		final ApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticCdr(null, "source1", true, Cdr.Type.EXT_EXT, 0),
						(Cdr) new StaticCdr(null, "source2", true, Cdr.Type.EXT_EXT, 0)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<Cdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(Cdr.Type.EXT_EXT));
	}
	
}
