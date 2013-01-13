package nl.limesco.cserv.invoice.transform;

import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.amdatu.template.processor.TemplateEngine;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(InvoiceTransformationService.class.getName(), null)
				.setImplementation(InvoiceTransformationServiceImpl.class)
				.add(createServiceDependency().setService(TemplateEngine.class).setRequired(true))
				.add(createServiceDependency().setService(PdfLatex.class).setRequired(true)));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
