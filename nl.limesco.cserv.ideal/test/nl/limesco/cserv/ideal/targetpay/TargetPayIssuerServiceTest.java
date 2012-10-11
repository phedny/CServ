package nl.limesco.cserv.ideal.targetpay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TargetPayIssuerServiceTest {
	
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private TargetPayIssuerServiceImpl service;
	
	private DependencyManager dependencyManager;
	
	@Before
	public void setUp() throws Exception {
		service = new TargetPayIssuerServiceImpl();
		
		dependencyManager = context.mock(DependencyManager.class);
		final Field dependencyManagerField = service.getClass().getDeclaredField("dependencyManager");
		dependencyManagerField.setAccessible(true);
		dependencyManagerField.set(service, dependencyManager);
	}

	@Test
	public void issuerListCanBeInitialized() throws Exception {
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

	@Test
	public void issuerCanBeAdded() throws Exception {
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

	@Test
	public void issuerCanBeRemoved() throws Exception {
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

	@Test
	public void issuerCanBeUpdated() throws Exception {
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
