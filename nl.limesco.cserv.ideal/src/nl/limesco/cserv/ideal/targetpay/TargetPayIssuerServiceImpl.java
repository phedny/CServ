package nl.limesco.cserv.ideal.targetpay;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.limesco.cserv.ideal.api.Issuer;
import nl.limesco.cserv.lib.quartz.annotations.StartNow;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatForever;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatInterval;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Throwables;

@StartNow
@RepeatForever
@RepeatInterval(period = RepeatInterval.HOUR, value = 4)
public class TargetPayIssuerServiceImpl implements Job {
	
	private static final String ISSUER_LIST_URL = "https://www.targetpay.com/ideal/getissuers.php?format=xml";
	
	private final XPathExpression ISSUER_XPATH_EXPR;

	private volatile DependencyManager dependencyManager;
	
	private final Map<String, Component> issuers = new HashMap<String, Component>();
	
	public TargetPayIssuerServiceImpl() {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xPath = factory.newXPath();
		try {
			ISSUER_XPATH_EXPR = xPath.compile("/issuers/issuer");
		} catch (XPathExpressionException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			updateIssuerList(ISSUER_LIST_URL);
		} catch (ParserConfigurationException e) {
			throw new JobExecutionException(e);
		} catch (SAXException e) {
			throw new JobExecutionException(e);
		} catch (IOException e) {
			throw new JobExecutionException(e);
		} catch (XPathExpressionException e) {
			throw new JobExecutionException(e);
		}
	}

	private synchronized void updateIssuerList(String issuerListUrl) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		final Document issuerDocument = documentBuilder.parse(issuerListUrl);
		final NodeList issuerNodes = (NodeList) ISSUER_XPATH_EXPR.evaluate(issuerDocument, XPathConstants.NODESET);
		
		final Set<String> issuerIds = new HashSet<String>();
		for (int i = 0; i < issuerNodes.getLength(); i++) {
			issuerIds.add(processIssuerNode(issuerNodes.item(i)));
		}
		
		// Remove any issuer that doesn't exist anymore
		for (Entry<String, Component> issuerEntry : issuers.entrySet()) {
			if (!issuerIds.contains(issuerEntry.getKey())) {
				dependencyManager.remove(issuerEntry.getValue());
			}
		}
	}

	private String processIssuerNode(Node issuerNode) {
		final String identifier = issuerNode.getAttributes().getNamedItem("id").getTextContent();
		final String name = issuerNode.getTextContent();
		
		final Component issuerComponent = issuers.get(identifier);
		if (issuerComponent == null) {
			// Create new issuer component
			createIssuerComponent(identifier, name);
		} else {
			IssuerImpl issuerObject = (IssuerImpl) issuerComponent.getService();
			if (!name.equals(issuerObject.getName())) {
				// Replace existing issuer component
				dependencyManager.remove(issuerComponent);
				createIssuerComponent(identifier, name);
			}
		}
		
		return identifier;
	}

	private void createIssuerComponent(String identifier, String name) {
		final IssuerImpl issuerObject = new IssuerImpl(identifier, name);
		final Component issuerComponent = dependencyManager.createComponent()
				.setInterface(Issuer.class.getName(), null)
				.setImplementation(issuerObject);
		dependencyManager.add(issuerComponent);
		issuers.put(identifier, issuerComponent);
	}

}
