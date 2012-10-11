/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.luminis.useradmin.mongodb.osgi;

import java.util.Properties;

import net.luminis.useradmin.mongodb.MongoDBRepositoryStore;

import org.amdatu.mongo.MongoDBService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.useradmin.RoleRepositoryStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.useradmin.UserAdminListener;


public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {
		Properties properties = new Properties();
        properties.put(Constants.SERVICE_PID, MongoDBRepositoryStore.PID);

		String[] serviceNames = { RoleRepositoryStore.class.getName(), UserAdminListener.class.getName(), ManagedService.class.getName() };

		manager.add(createComponent()
				.setInterface(serviceNames, properties)
				.setImplementation(MongoDBRepositoryStore.class)
				.add(createServiceDependency()
						.setService(MongoDBService.class)
						.setRequired(true))
				.add(createServiceDependency()
						.setService(LogService.class)
						.setRequired(false))
				);
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// Nop
	}
}
