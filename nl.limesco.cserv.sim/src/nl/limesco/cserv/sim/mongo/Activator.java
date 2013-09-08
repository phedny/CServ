package nl.limesco.cserv.sim.mongo;

import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.sim.api.SimService;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(SimService.class.getName(), null)
				.setImplementation(SimServiceImpl.class)
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true)));
		manager.add(createComponent()
				.setInterface(AccountMergeHelper.class.getName(), null)
				.setImplementation(SimAccountMergeHelper.class)
				.add(createServiceDependency().setService(SimService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
