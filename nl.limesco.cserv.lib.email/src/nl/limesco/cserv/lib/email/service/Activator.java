package nl.limesco.cserv.lib.email.service;

import nl.limesco.cserv.lib.email.api.EmailService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(EmailService.class.getName(), null)
				.setImplementation(EmailServiceImpl.class)
				.add(createConfigurationDependency().setPid("nl.limesco.cserv.lib.email")));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
