package nl.limesco.cserv.ideal.targetpay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import nl.limesco.cserv.ideal.api.Issuer;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.legacy.ClassImposteriser;

public class TargetPayIssuerServiceTest extends MockObjectTestCase {
	
	private Mockery context = new JUnit3Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private TargetPayIssuerServiceImpl service;
	
	private DependencyManager dependencyManager;
	
	public void setUp() throws Exception {
		service = new TargetPayIssuerServiceImpl();
		
		dependencyManager = context.mock(DependencyManager.class);
		final Field dependencyManagerField = service.getClass().getDeclaredField("dependencyManager");
		dependencyManagerField.setAccessible(true);
		dependencyManagerField.set(service, dependencyManager);
	}

	public void testIssuerListCanBeInitialized() throws Exception {
		context.checking(new Expectations() {{
			Component component = context.mock(Component.class);
			ignoring (component);
			
			exactly(2).of (dependencyManager).createComponent(); will(returnValue(component));
			exactly(2).of (dependencyManager).add(with(any(Component.class)));
		}});
		
		final URL issuersResource = getClass().getClassLoader().getResource("nl/limesco/cserv/ideal/targetpay/issuers.xml");
		final Method updateIssuerList = service.getClass().getDeclaredMethod("updateIssuerList", String.class);
		updateIssuerList.setAccessible(true);
		updateIssuerList.invoke(service, issuersResource.toString());
	}

	public void testIssuerCanBeAdded() throws Exception {
		final Field issuersField = service.getClass().getDeclaredField("issuers");
		issuersField.setAccessible(true);
		final Map<String, Component> issuers = (Map<String, Component>) issuersField.get(service);
		issuers.put("0001", new StubComponent(new IssuerImpl("0001", "First Bank")));
		
		context.checking(new Expectations() {{
			Component component = context.mock(Component.class);
			ignoring (component);
			
			oneOf (dependencyManager).createComponent(); will(returnValue(component));
			oneOf (dependencyManager).add(with(any(Component.class)));
		}});
		
		final URL issuersResource = getClass().getClassLoader().getResource("nl/limesco/cserv/ideal/targetpay/issuers.xml");
		final Method updateIssuerList = service.getClass().getDeclaredMethod("updateIssuerList", String.class);
		updateIssuerList.setAccessible(true);
		updateIssuerList.invoke(service, issuersResource.toString());
	}

	public void testIssuerCanBeRemoved() throws Exception {
		final Field issuersField = service.getClass().getDeclaredField("issuers");
		issuersField.setAccessible(true);
		final Map<String, Component> issuers = (Map<String, Component>) issuersField.get(service);
		issuers.put("0001", new StubComponent(new IssuerImpl("0001", "First Bank")));
		issuers.put("0002", new StubComponent(new IssuerImpl("0002", "Second Bank")));
		issuers.put("0003", new StubComponent(new IssuerImpl("0003", "Third Bank")));
		
		context.checking(new Expectations() {{
			oneOf (dependencyManager).remove(with(any(Component.class)));
		}});
		
		final URL issuersResource = getClass().getClassLoader().getResource("nl/limesco/cserv/ideal/targetpay/issuers.xml");
		final Method updateIssuerList = service.getClass().getDeclaredMethod("updateIssuerList", String.class);
		updateIssuerList.setAccessible(true);
		updateIssuerList.invoke(service, issuersResource.toString());
	}

	public void testIssuerCanBeUpdated() throws Exception {
		final Field issuersField = service.getClass().getDeclaredField("issuers");
		issuersField.setAccessible(true);
		final Map<String, Component> issuers = (Map<String, Component>) issuersField.get(service);
		issuers.put("0001", new StubComponent(new IssuerImpl("0001", "First Bank")));
		issuers.put("0002", new StubComponent(new IssuerImpl("0002", "Alternative Second Bank")));
		
		context.checking(new Expectations() {{
			Component component = context.mock(Component.class);
			ignoring (component);
			
			oneOf (dependencyManager).createComponent(); will(returnValue(component));
			oneOf (dependencyManager).add(with(any(Component.class)));
			oneOf (dependencyManager).remove(with(any(Component.class)));
		}});
		
		final URL issuersResource = getClass().getClassLoader().getResource("nl/limesco/cserv/ideal/targetpay/issuers.xml");
		final Method updateIssuerList = service.getClass().getDeclaredMethod("updateIssuerList", String.class);
		updateIssuerList.setAccessible(true);
		updateIssuerList.invoke(service, issuersResource.toString());
	}

}
