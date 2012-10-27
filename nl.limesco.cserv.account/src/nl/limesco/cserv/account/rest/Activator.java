package nl.limesco.cserv.account.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.payment.api.PaymentService;
import nl.limesco.cserv.util.dm.UnavailableOSGiService;
import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(Object.class.getName(), null)
				.setImplementation(AccountsResource.class)
				.add(createServiceDependency().setService(WebAuthorizationService.class).setRequired(true))
				.add(createServiceDependency().setService(PdfLatex.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(PdfLatex.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(AccountService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(AccountService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(InvoiceService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(InvoiceService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE)))
				.add(createServiceDependency().setService(PaymentService.class).setRequired(false).setDefaultImplementation(UnavailableOSGiService.newInstance(PaymentService.class, WebApplicationException.class, Status.SERVICE_UNAVAILABLE))));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
