package nl.limesco.cserv.rest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void destroy(BundleContext context, DependencyManager manager)
			throws Exception {
	}

	@Override
	public void init(BundleContext context, DependencyManager manager)
			throws Exception {
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(DocumentationResource.class));
	}

}
