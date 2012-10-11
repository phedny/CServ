package nl.limesco.cserv.ideal.targetpay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import nl.limesco.cserv.ideal.api.Currency;
import nl.limesco.cserv.ideal.api.IdealException;
import nl.limesco.cserv.ideal.api.Issuer;
import nl.limesco.cserv.ideal.api.Transaction;
import nl.limesco.cserv.ideal.api.TransactionStatus;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class IdealServiceTest {
	
	private Mockery context = new JUnit4Mockery();
	
	private URL startUrl;
	
	private URL checkUrl;
	
	private URL returnUrl;
	
	private URL redirectUrl;

	private IdealServiceImpl service;
	
	private IdealHttpTool httpTool;
	
	private Issuer issuer = new IssuerImpl("0001", "First Bank");
	
	@Before
	public void setUp() throws Exception {
		httpTool = context.mock(IdealHttpTool.class);
		
		startUrl = new URL("http://start");
		checkUrl = new URL("http://check");
		returnUrl = new URL("http://return");
		redirectUrl = new URL("http://redirect");
		service = new IdealServiceImpl(startUrl, checkUrl, httpTool);
		
		final Properties properties = new Properties();
		properties.put("layout_code", "42");
		service.updated(properties);
	}
	
	@Test
	public void canCreateTransaction() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(startUrl, "rtlo=42&bank=0001&description=Test%20description&currency=EUR&amount=1337&returnurl=http://return"); will(returnValue("000000 123|http://redirect"));
		}});
		
		final Transaction tx = service.createTransaction(issuer, Currency.EUR, 1337, "Test description", returnUrl);
		assertEquals("123", tx.getTransactionId());
		assertEquals(Currency.EUR, tx.getCurrency());
		assertEquals(1337, tx.getAmount());
		assertEquals(redirectUrl, tx.getRedirectUrl());
		assertEquals(returnUrl, tx.getReturnUrl());
	}

	@Test
	public void cannotCreateTransactionWithError() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(startUrl, "rtlo=42&bank=0001&description=Test%20description&currency=EUR&amount=1337&returnurl=http://return"); will(returnValue("SO1000 Storing in systeem"));
		}});
		
		try {
			service.createTransaction(issuer, Currency.EUR, 1337, "Test description", returnUrl);
			fail("Expected IdealException");
		} catch (IdealException e) {
			assertEquals("SO1000 Storing in systeem", e.getMessage());
		}
	}

	@Test
	public void transactionCanBeCompleted() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("000000 OK"));
		}});
		
		final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
		assertEquals(TransactionStatus.COMPLETED, service.getTransactionStatus(tx));
	}

	@Test
	public void transactionCanBeOpen() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("TP0010 Transactie is nog niet afgerond, probeer het later opnieuw"));
		}});
		
		final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
		assertEquals(TransactionStatus.OPEN, service.getTransactionStatus(tx));
	}

	@Test
	public void transactionCanBeCancelled() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("TP0011 Transactie is geannuleerd"));
		}});
		
		final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
		assertEquals(TransactionStatus.CANCELLED, service.getTransactionStatus(tx));
	}

	@Test
	public void transactionCanExpire() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("TP0012 Transactie is verlopen (max. 10 minuten)"));
		}});
		
		final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
		assertEquals(TransactionStatus.EXPIRED, service.getTransactionStatus(tx));
	}

	@Test
	public void transactionCanFail() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("TP0013 De transactie kon niet verwerkt worden"));
		}});
		
		final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
		assertEquals(TransactionStatus.FAILED, service.getTransactionStatus(tx));
	}

	@Test
	public void cannotCheckTransactionWithError() throws IOException, IdealException {
		context.checking(new Expectations() {{
			oneOf (httpTool).doIdeal(checkUrl, "rtlo=42&trxid=123"); will(returnValue("SO1000 Storing in systeem"));
		}});
		
		try {
			final Transaction tx = new TransactionImpl("123", issuer, Currency.EUR, 1337, returnUrl, redirectUrl);
			service.getTransactionStatus(tx);
			fail("Expected IdealException");
		} catch (IdealException e) {
			assertEquals("SO1000 Storing in systeem", e.getMessage());
		}
	}

}
