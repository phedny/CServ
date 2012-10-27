package nl.limesco.cserv.util.pdflatex.impl;

import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		manager.add(createComponent()
				.setInterface(PdfLatex.class.getName(), null)
				.setImplementation(PdfLatexImpl.class)
				.add(createConfigurationDependency().setPid("nl.limesco.cserv.util.pdflatex")));
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
	}

}
