package nl.limesco.cserv.cdr.transform.account;

import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.CdrRetriever;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createAspectService(CdrRetriever.class, null, 50)
				.setImplementation(ExternalAccountRetrieverAspect.class)
				.add(createServiceDependency().setService(AccountService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
