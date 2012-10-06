package nl.limesco.cserv.ideal.targetpay;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.ComponentStateListener;
import org.apache.felix.dm.Dependency;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.ServiceRegistration;

public class StubComponent implements Component {
	
	private final Object service;
	
	public StubComponent(Object service) {
		this.service = service;
	}

	@Override
	public Component add(Dependency arg0) {
		return null;
	}

	@Override
	public Component add(List arg0) {
		return null;
	}

	@Override
	public void addStateListener(ComponentStateListener arg0) {
	}

	@Override
	public boolean getAutoConfig(Class arg0) {
		return false;
	}

	@Override
	public String getAutoConfigInstance(Class arg0) {
		return null;
	}

	@Override
	public Object[] getCompositionInstances() {
		return null;
	}

	@Override
	public List getDependencies() {
		return null;
	}

	@Override
	public DependencyManager getDependencyManager() {
		return null;
	}

	@Override
	public Object getService() {
		return service;
	}

	@Override
	public Dictionary getServiceProperties() {
		return null;
	}

	@Override
	public ServiceRegistration getServiceRegistration() {
		return null;
	}

	@Override
	public void invokeCallbackMethod(Object[] arg0, String arg1, Class[][] arg2, Object[][] arg3) {
	}

	@Override
	public Component remove(Dependency arg0) {
		return null;
	}

	@Override
	public void removeStateListener(ComponentStateListener arg0) {
	}

	@Override
	public Component setAutoConfig(Class arg0, boolean arg1) {
		return null;
	}

	@Override
	public Component setAutoConfig(Class arg0, String arg1) {
		return null;
	}

	@Override
	public Component setCallbacks(String arg0, String arg1, String arg2, String arg3) {
		return null;
	}

	@Override
	public Component setCallbacks(Object arg0, String arg1, String arg2, String arg3, String arg4) {
		return null;
	}

	@Override
	public Component setComposition(String arg0) {
		return null;
	}

	@Override
	public Component setComposition(Object arg0, String arg1) {
		return null;
	}

	@Override
	public Component setFactory(String arg0) {
		return null;
	}

	@Override
	public Component setFactory(Object arg0, String arg1) {
		return null;
	}

	@Override
	public Component setImplementation(Object arg0) {
		return null;
	}

	@Override
	public Component setInterface(String arg0, Dictionary arg1) {
		return null;
	}

	@Override
	public Component setInterface(String[] arg0, Dictionary arg1) {
		return null;
	}

	@Override
	public Component setServiceProperties(Dictionary arg0) {
		return null;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
