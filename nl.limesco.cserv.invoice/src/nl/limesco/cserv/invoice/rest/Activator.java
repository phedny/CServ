package nl.limesco.cserv.invoice.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.invoice.api.InvoiceConstructor;
import nl.limesco.cserv.invoice.api.InvoiceReportService;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.util.dm.UnavailableOSGiService;

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
				.add(createServiceDependency().setService(InvoiceTransformationService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceTransformationService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(InvoiceConstructor.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceConstructor.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(BatchInvoicingService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(BatchInvoicingService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
		
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(InvoiceReportResource.class)
				.add(createServiceDependency().setService(WebAuthorizationService.class).setRequired(true))
				.add(createServiceDependency().setService(InvoiceReportService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceReportService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
