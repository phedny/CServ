package nl.limesco.cserv.payment.mongo;

import java.util.Set;

import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentBuilder;
import nl.limesco.cserv.payment.api.PaymentStatus;
import nl.limesco.cserv.payment.api.PaymentType;

public class PaymentBuilderImpl implements PaymentBuilder {
	private final PaymentImpl payment;
	
	public PaymentBuilderImpl() {
		payment = new PaymentImpl();
		payment.setAmount(0);
		payment.setCurrency(InvoiceCurrency.EUR);
		payment.setStatus(PaymentStatus.Open);
	}

	public PaymentBuilderImpl(Payment copy) {
		this();
		accountId(copy.getAccountId());
		currency(copy.getCurrency());
		amount(copy.getAmount());
		transactionId(copy.getTransactionId());
		destination(copy.getDestination());
		paymentType(copy.getPaymentType());
		setInvoiceIds(copy.getInvoiceIds());
	}
	
	@Override
	public PaymentBuilder accountId(String accountId) {
		payment.setAccountId(accountId);
		return this;
	}

	@Override
	public PaymentBuilder currency(InvoiceCurrency currency) {
		payment.setCurrency(currency);
		return this;
	}

	@Override
	public PaymentBuilder amount(int amount) {
		payment.setAmount(amount);
		return this;
	}

	@Override
	public PaymentBuilder transactionId(String id) {
		payment.setTransactionId(id);
		return this;
	}

	@Override
	public PaymentBuilder destination(String destination) {
		payment.setDestination(destination);
		return this;
	}

	@Override
	public PaymentBuilder paymentType(PaymentType type) {
		payment.setPaymentType(type);
		return this;
	}

	@Override
	public PaymentBuilder setInvoiceIds(Set<String> invoiceIds) {
		payment.setInvoiceIds(invoiceIds);
		return this;
	}

	@Override
	public PaymentBuilder addInvoiceId(String invoiceId) {
		payment.addInvoiceId(invoiceId);
		return this;
	}

	@Override
	public Payment build() {
		return payment;
	}

}
