package nl.limesco.cserv.invoicing;

import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.lib.email.api.EmailService;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.sim.api.SimService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(BatchInvoicingService.class.getName(), null)
				.setImplementation(BatchInvoicingServiceImpl.class)
				.setComposition("getComposition")
				.add(createServiceDependency().setService(EmailService.class).setRequired(true))
				.add(createServiceDependency().setService(InvoiceService.class).setRequired(true))
				.add(createServiceDependency().setService(AccountService.class).setRequired(true))
				.add(createServiceDependency().setService(PricingService.class).setRequired(true))
				.add(createServiceDependency().setService(SimService.class).setRequired(true))
				.add(createServiceDependency().setService(CdrService.class).setRequired(true))
				.add(createServiceDependency().setService(InvoiceTransformationService.class).setRequired(true))
				.add(createServiceDependency().setService(LogService.class).setRequired(false)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
