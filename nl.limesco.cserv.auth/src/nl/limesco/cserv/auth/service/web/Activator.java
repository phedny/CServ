package nl.limesco.cserv.auth.service.web;

import nl.limesco.cserv.auth.api.AuthorizationService;
import nl.limesco.cserv.auth.api.WebAuthorizationService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(WebAuthorizationService.class.getName(), null)
				.setImplementation(WebAuthorizationServiceImpl.class)
				.add(createServiceDependency().setService(AuthorizationService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
