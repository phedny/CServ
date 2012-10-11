package org.amdatu.legacy.tenantuseradmin;

import java.util.Properties;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.useradmin.UserAdmin;

public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		String filter = "(" + Constants.OBJECTCLASS + "=" + UserAdmin.class.getName() + ")";

		Properties props = new Properties();
		props.put("tenant_id", "Default");

		manager.add(createAspectService(UserAdmin.class, filter, 10)
				.setImplementation(UserAdminWrapper.class)
				.setServiceProperties(props)
				);
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// Nop
	}
}
