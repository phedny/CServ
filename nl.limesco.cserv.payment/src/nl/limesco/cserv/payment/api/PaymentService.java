package nl.limesco.cserv.payment.api;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

public interface PaymentService {
	Optional<? extends Payment> getPaymentById(String id);
	Collection<? extends Payment> getPaymentsByAccountId(String accountId);
	
	PaymentBuilder buildPayment();
	PaymentBuilder buildPayment(Payment p);
	
	void updatePayment(Payment payment);
	Payment createPaymentFromJson(String json) throws IOException;
}
