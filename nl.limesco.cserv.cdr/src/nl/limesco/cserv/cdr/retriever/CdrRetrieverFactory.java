package nl.limesco.cserv.cdr.retriever;

import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;

import nl.limesco.cserv.cdr.api.CdrRetriever;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;

import com.google.common.collect.Maps;

public class CdrRetrieverFactory implements ManagedServiceFactory {
	
	public static final String PID = "nl.limesco.cserv.cdr.retriever";
	
	private final Map<String, Component> components = Maps.newHashMap();
	
	private volatile DependencyManager dependencyManager;

	@Override
	public String getName() {
		return PID;
	}

	@Override
	public synchronized void updated(String pid, Dictionary properties) throws ConfigurationException {
		final String source = (String) properties.get("source");
		
		if (components.containsKey(pid)) {
			final Component component = components.get(pid);
			if (source.equals(component.getServiceProperties().get("source"))) {
				// Reconfigure existing retriever
				((ConfigurableHttpCdrRetriever) component.getService()).configure(properties);
				return;
			} else {
				// Remove existing retriever and have a new one created
				deleted(pid);
			}
		}
		
		final ConfigurableHttpCdrRetriever retrieverInstance = new ConfigurableHttpCdrRetriever();
		retrieverInstance.configure(properties);

		final Properties componentProps = new Properties();
		componentProps.put("source", source);
		
		final Component component = dependencyManager.createComponent()
				.setInterface(CdrRetriever.class.getName(), componentProps)
				.setImplementation(retrieverInstance)
				.add(dependencyManager.createServiceDependency().setService(LogService.class).setRequired(false));
		dependencyManager.add(component);
		components.put(pid, component);
	}

	@Override
	public synchronized void deleted(String pid) {
		final Component component = components.remove(pid);
		dependencyManager.remove(component);
	}

}
