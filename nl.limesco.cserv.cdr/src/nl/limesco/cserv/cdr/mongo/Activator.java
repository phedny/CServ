package nl.limesco.cserv.cdr.mongo;

import nl.limesco.cserv.cdr.api.CdrService;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(CdrService.class.getName(), null)
				.setImplementation(CdrServiceImpl.class)
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
