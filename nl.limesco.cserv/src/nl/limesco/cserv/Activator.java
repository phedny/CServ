package nl.limesco.cserv;

import nl.limesco.cserv.invoice.api.BatchInvoicingService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.quartz.Job;

public class Activator extends DependencyActivatorBase {

	@Override
	public void destroy(BundleContext arg0, DependencyManager arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(BundleContext arg0, DependencyManager arg1)
			throws Exception {
		// TODO Auto-generated method stub
		arg1.add(createComponent()
				.setInterface(Job.class.getName(), null)
				.setImplementation(Perform.class)
				.add(createServiceDependency().setService(BatchInvoicingService.class).setRequired(true)));
	}

}
