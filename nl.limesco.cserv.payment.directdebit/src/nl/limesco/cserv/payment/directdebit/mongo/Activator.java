package nl.limesco.cserv.payment.directdebit.mongo;

import nl.limesco.cserv.payment.directdebit.api.MandateService;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(MandateService.class.getName(), null)
				.setImplementation(MongoMandateService.class)
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true))
				.add(createConfigurationDependency().setPid(MongoMandateService.PID)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
