package nl.limesco.cserv.auth.service;

import nl.limesco.cserv.auth.api.AuthorizationService;

import org.amdatu.auth.tokenprovider.TokenProvider;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.useradmin.UserAdmin;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(AuthorizationService.class.getName(), null)
				.setImplementation(AuthorizationServiceImpl.class)
				.add(createServiceDependency().setService(UserAdmin.class).setRequired(true))
				.add(createServiceDependency().setService(TokenProvider.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
