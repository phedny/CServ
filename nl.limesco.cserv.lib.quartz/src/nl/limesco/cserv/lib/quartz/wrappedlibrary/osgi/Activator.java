/*******************************************************************************
 * Copyright (c) 2012 Lopexs.
 * All rights reserved.
 ******************************************************************************/
package nl.limesco.cserv.lib.quartz.wrappedlibrary.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        org.slf4j.impl.OSGILogFactory.initOSGI(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
