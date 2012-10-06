package nl.limesco.cserv.ideal.rest;

import nl.limesco.cserv.ideal.api.Issuer;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(IdealResource.class)
				.add(createServiceDependency().setService(Issuer.class).setCallbacks("addIssuer", "removeIssuer")));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
