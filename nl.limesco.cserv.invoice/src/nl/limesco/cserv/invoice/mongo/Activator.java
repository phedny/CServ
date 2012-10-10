package nl.limesco.cserv.invoice.mongo;

import nl.limesco.cserv.invoice.api.InvoiceService;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(InvoiceService.class.getName(), null)
				.setImplementation(InvoiceServiceImpl.class)
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
