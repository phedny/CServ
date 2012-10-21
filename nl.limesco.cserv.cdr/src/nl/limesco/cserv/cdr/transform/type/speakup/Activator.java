package nl.limesco.cserv.cdr.transform.type.speakup;

import nl.limesco.cserv.cdr.api.CdrRetriever;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createAspectService(CdrRetriever.class, "(source=speakup)", 30)
				.setImplementation(SpeakUpTypeAspect.class));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
