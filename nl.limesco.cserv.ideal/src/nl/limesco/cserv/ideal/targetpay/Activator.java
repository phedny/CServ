package nl.limesco.cserv.ideal.targetpay;

import nl.limesco.cserv.ideal.api.IdealService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.quartz.Job;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(Job.class.getName(), null)
				.setImplementation(TargetPayIssuerServiceImpl.class));
		
		manager.add(createComponent()
				.setInterface(IdealService.class.getName(), null)
				.setImplementation(IdealServiceImpl.class)
				.add(createConfigurationDependency().setPid("nl.limesco.cserv.ideal.targetpay")));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
