package nl.limesco.cserv.pricing.mongo;

import nl.limesco.cserv.pricing.api.PricingService;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(PricingService.class.getName(), null)
				.setImplementation(PricingServiceImpl.class)
				.setComposition("getComposition")
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
