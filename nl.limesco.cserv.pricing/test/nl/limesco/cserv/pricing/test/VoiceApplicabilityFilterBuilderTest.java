package nl.limesco.cserv.pricing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilter;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.mongo.VoiceApplicabilityFilterBuilderImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class VoiceApplicabilityFilterBuilderTest {

	private VoiceApplicabilityFilterBuilderImpl builder;
	
	@Before
	public void setUp() {
		builder = new VoiceApplicabilityFilterBuilderImpl();
	}

	@Test
	public void defaultFilterDoesNotMatchAnything() {
		final VoiceApplicabilityFilter filter = builder.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void anyFilterMatchesAnything() {
		final VoiceApplicabilityFilter filter = builder
				.source(VoiceApplicabilityFilterBuilder.ANY)
				.callConnectivityType(VoiceApplicabilityFilterBuilder.ANY)
				.cdrType(VoiceApplicabilityFilterBuilder.ANY)
				.destination(VoiceApplicabilityFilterBuilder.ANY)
				.build();
		assertFalse(filter.getSources().isPresent());
		assertFalse(filter.getCallConnectivityTypes().isPresent());
		assertFalse(filter.getCdrTypes().isPresent());
		assertFalse(filter.getDestinations().isPresent());
	}
	
	@Test
	public void sourceFilterMatchesOneSource() {
		final VoiceApplicabilityFilter filter = builder
				.source("source1")
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoSourcesFilterMatchesTwoSources() {
		final VoiceApplicabilityFilter filter = builder
				.source("source1")
				.source("source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoVarargsSourcesFilterMatchesTwoSources() {
		final VoiceApplicabilityFilter filter = builder
				.source("source1", "source2")
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoCollectionSourcesFilterMatchesTwoSources() {
		final VoiceApplicabilityFilter filter = builder
				.source(Lists.newArrayList("source1", "source2"))
				.build();
		final Collection<String> set = filter.getSources().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("source1"));
		assertTrue(set.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void callConnectivityTypeFilterMatchesOneCallConnectivityType() {
		final VoiceApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertEquals(1, filter.getCallConnectivityTypes().get().size());
		assertEquals(CallConnectivityType.OOTB, filter.getCallConnectivityTypes().get().iterator().next());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final VoiceApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB)
				.callConnectivityType(CallConnectivityType.DIY)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoVarargsCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final VoiceApplicabilityFilter filter = builder
				.callConnectivityType(CallConnectivityType.OOTB, CallConnectivityType.DIY)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoCollectionCallConnectivityTypesFilterMatchesTwoCallConnectivityTypes() {
		final VoiceApplicabilityFilter filter = builder
				.callConnectivityType(Lists.newArrayList(CallConnectivityType.OOTB, CallConnectivityType.DIY))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		final Collection<CallConnectivityType> set = filter.getCallConnectivityTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(CallConnectivityType.OOTB));
		assertTrue(set.contains(CallConnectivityType.DIY));
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void cdrTypeFilterMatchesOneCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdrType(VoiceCdr.Type.EXT_EXT)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(VoiceCdr.Type.EXT_EXT, filter.getCdrTypes().get().iterator().next());
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoCdrTypesFilterMatchesTwoCdrTypes() {
		final VoiceApplicabilityFilter filter = builder
				.cdrType(VoiceCdr.Type.EXT_EXT)
				.cdrType(VoiceCdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(set.contains(VoiceCdr.Type.EXT_MOBILE));
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoVarargsCdrTypesFilterMatchesTwoCdrTypes() {
		final VoiceApplicabilityFilter filter = builder
				.cdrType(VoiceCdr.Type.EXT_EXT, VoiceCdr.Type.EXT_MOBILE)
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(set.contains(VoiceCdr.Type.EXT_MOBILE));
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void twoCollectionCdrTypesFilterMatchesTwoCdrTypes() {
		final VoiceApplicabilityFilter filter = builder
				.cdrType(Lists.newArrayList(VoiceCdr.Type.EXT_EXT, VoiceCdr.Type.EXT_MOBILE))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> set = filter.getCdrTypes().get();
		assertEquals(2, set.size());
		assertTrue(set.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(set.contains(VoiceCdr.Type.EXT_MOBILE));
		assertTrue(filter.getDestinations().get().isEmpty());
	}

	@Test
	public void destinationFilterMatchesOneDestinaion() {
		final VoiceApplicabilityFilter filter = builder
				.destination("Middle Earth")
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoDestinationsFilterMatchesTwoDestinations() {
		final VoiceApplicabilityFilter filter = builder
				.destination("Planet Jupiter")
				.destination("Planet Saturn")
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		final Collection<String> set = filter.getDestinations().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("Planet Jupiter"));
		assertTrue(set.contains("Planet Saturn"));
	}

	@Test
	public void twoVarargsDestinationsFilterMatchesTwoDestinations() {
		final VoiceApplicabilityFilter filter = builder
				.destination("Planet Jupiter", "Planet Saturn")
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		final Collection<String> set = filter.getDestinations().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("Planet Jupiter"));
		assertTrue(set.contains("Planet Saturn"));
	}

	@Test
	public void twoCollectionDestinationsFilterMatchesTwoDestinations() {
		final VoiceApplicabilityFilter filter = builder
				.destination(Lists.newArrayList("Planet Jupiter", "Planet Saturn"))
				.build();
		assertTrue(filter.getSources().get().isEmpty());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertTrue(filter.getCdrTypes().get().isEmpty());
		final Collection<String> set = filter.getDestinations().get();
		assertEquals(2, set.size());
		assertTrue(set.contains("Planet Jupiter"));
		assertTrue(set.contains("Planet Saturn"));
	}

	@Test
	public void cdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(new StaticVoiceCdr(null, "source1", true, true, 0))
				.build();
		assertEquals(1, filter.getSources().get().size());
		assertEquals("source1", filter.getSources().get().iterator().next());
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		assertEquals(1, filter.getCdrTypes().get().size());
		assertEquals(VoiceCdr.Type.EXT_EXT, filter.getCdrTypes().get().iterator().next());
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoDisjointCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0))
				.cdr(new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_MOBILE, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_MOBILE));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoDisjointVarargsCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0),
						new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_MOBILE, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_MOBILE));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoDisjointCollectionCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0),
						(Cdr) new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_MOBILE, 0)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(2, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_MOBILE));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoOverlappingCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0))
				.cdr(new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_EXT, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoOverlappingVarargsCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0),
						new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_EXT, 0))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}

	@Test
	public void twoOverlappingCollectionCdrFilterMatchesSourceAndCdrType() {
		final VoiceApplicabilityFilter filter = builder
				.cdr(Lists.newArrayList(
						(Cdr) new StaticVoiceCdr(null, "source1", true, VoiceCdr.Type.EXT_EXT, 0),
						(Cdr) new StaticVoiceCdr(null, "source2", true, VoiceCdr.Type.EXT_EXT, 0)))
				.build();
		final Collection<String> sourceSet = filter.getSources().get();
		assertEquals(2, sourceSet.size());
		assertTrue(sourceSet.contains("source1"));
		assertTrue(sourceSet.contains("source2"));
		assertTrue(filter.getCallConnectivityTypes().get().isEmpty());
		final Collection<VoiceCdr.Type> cdrTypeSet = filter.getCdrTypes().get();
		assertEquals(1, cdrTypeSet.size());
		assertTrue(cdrTypeSet.contains(VoiceCdr.Type.EXT_EXT));
		assertEquals(1, filter.getDestinations().get().size());
		assertEquals("Middle Earth", filter.getDestinations().get().iterator().next());
	}
	
}
