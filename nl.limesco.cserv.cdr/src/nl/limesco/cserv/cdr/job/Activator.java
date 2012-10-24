package nl.limesco.cserv.cdr.job;

import nl.limesco.cserv.cdr.api.CdrRetriever;
import nl.limesco.cserv.cdr.api.CdrService;

import org.amdatu.mongo.MongoDBService;
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
				.setImplementation(CdrRetrievalJob.class)
				.add(createServiceDependency().setService(JobStateService.class).setRequired(true))
				.add(createServiceDependency().setService(CdrService.class).setRequired(true))
				.add(createServiceDependency().setService(LogService.class).setRequired(false))
				.add(createServiceDependency().setService(CdrRetriever.class).setRequired(true).setCallbacks("retrieverAdded", "retrieverRemoved")));
		
		manager.add(createComponent()
				.setInterface(JobStateService.class.getName(), null)
				.setImplementation(JobStateServiceImpl.class)
				.add(createServiceDependency().setService(MongoDBService.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
