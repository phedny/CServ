package nl.limesco.cserv.invoice.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.util.dm.UnavailableOSGiService;

import org.amdatu.template.processor.TemplateEngine;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(AccountResourceExtension.class.getName(), null)
				.setImplementation(InvoiceResourceExtension.class)
				.add(createServiceDependency().setService(InvoiceService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(InvoiceTransformationService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceTransformationService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
