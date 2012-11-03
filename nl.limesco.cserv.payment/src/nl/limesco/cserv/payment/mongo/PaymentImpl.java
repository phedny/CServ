package nl.limesco.cserv.payment.mongo;

import java.util.Set;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentStatus;
import nl.limesco.cserv.payment.api.PaymentType;

public class PaymentImpl implements Payment {
	String id;
	String accountId;
	Set<String> invoiceIds;
	PaymentType paymentType;
	InvoiceCurrency currency;
	String destination;
	String transactionId;
	int amount;
	PaymentStatus status;
	
	@Id
	@ObjectId
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public Set<String> getInvoiceIds() {
		return invoiceIds;
	}
	public void setInvoiceIds(Set<String> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}
	public void addInvoiceId(String invoiceId) {
		this.invoiceIds.add(invoiceId);
	}
	
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType type) {
		this.paymentType = type;
	}
	
	public InvoiceCurrency getCurrency() {
		return currency;
	}
	public void setCurrency(InvoiceCurrency c) {
		this.currency = c;
	}
	
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String id) {
		this.transactionId = id;
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
}
