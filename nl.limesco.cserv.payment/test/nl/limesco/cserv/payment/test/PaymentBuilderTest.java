package nl.limesco.cserv.payment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentBuilder;
import nl.limesco.cserv.payment.api.PaymentStatus;
import nl.limesco.cserv.payment.mongo.PaymentBuilderImpl;

import org.junit.Test;

import com.google.common.collect.Sets;

public class PaymentBuilderTest {
	
	private PaymentBuilder builderImpl = new PaymentBuilderImpl();
	
	@Test
	public void canCreateEmptyPayment() {
		Payment p = builderImpl.build();
		assertEquals(p.getStatus(), PaymentStatus.OPEN);
	}
	
	
	@Test
	public void canAddInvoiceIds() {
		Set<String> newInvoiceIds = Sets.newHashSet();
		newInvoiceIds.add("one");
		newInvoiceIds.add("two");
		Payment p = builderImpl.setInvoiceIds(newInvoiceIds).addInvoiceId("three").addInvoiceId("two").build();
		Set<String> invoiceIds = p.getInvoiceIds();
		assertTrue(invoiceIds.contains("one"));
		assertTrue(invoiceIds.contains("two"));
		assertTrue(invoiceIds.contains("three"));
		assertTrue(invoiceIds.size() == 3);
	}
	
	@Test
	public void paymentanHaveAccountId() {
		assertEquals("id", builderImpl.accountId("id").build().getAccountId());
	}
}