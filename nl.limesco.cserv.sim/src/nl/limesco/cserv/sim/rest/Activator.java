package nl.limesco.cserv.sim.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.sim.api.SimService;
import nl.limesco.cserv.util.dm.UnavailableOSGiService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(AccountResourceExtension.class.getName(), null)
				.setImplementation(SimResourceExtension.class)
				.add(createServiceDependency().setService(SimService.class).setRequired(true)));
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(SimResource.class)
				.add(createServiceDependency().setService(WebAuthorizationService.class).setRequired(true))
				.add(createServiceDependency().setService(SimService.class).setRequired(true).setDefaultImplementation(UnavailableOSGiService.newInstance(SimService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
