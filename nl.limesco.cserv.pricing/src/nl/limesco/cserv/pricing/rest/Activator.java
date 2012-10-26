package nl.limesco.cserv.pricing.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.util.dm.UnavailableOSGiService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext contex, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(PricingResource.class)
				.add(createServiceDependency().setService(PricingService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(PricingService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
