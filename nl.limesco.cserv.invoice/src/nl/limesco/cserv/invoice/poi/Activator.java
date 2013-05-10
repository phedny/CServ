package nl.limesco.cserv.invoice.poi;

import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.invoice.api.InvoiceReportService;
import nl.limesco.cserv.invoice.api.InvoiceService;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(InvoiceReportService.class.getName(), null)
				.setImplementation(PoiInvoiceReportService.class)
				.add(createServiceDependency().setService(InvoiceService.class).setRequired(true))
				.add(createServiceDependency().setService(AccountService.class).setRequired(true))
				.add(createServiceDependency().setService(LogService.class).setRequired(false)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
