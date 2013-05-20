package nl.limesco.cserv.payment.directdebit.rest;

import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.payment.directdebit.api.MandateService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(AccountResourceExtension.class.getName(), null)
				.setImplementation(MandateResourceExtension.class)
				.add(createServiceDependency().setService(MandateService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
