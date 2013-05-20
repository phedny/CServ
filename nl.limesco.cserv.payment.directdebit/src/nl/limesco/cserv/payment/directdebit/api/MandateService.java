package nl.limesco.cserv.payment.directdebit.api;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

public interface MandateService {
	
	Optional<? extends Mandate> getMandateById(String mandateId);

	Collection<? extends Mandate> getMandatesForAccount(String accountId);

	Collection<? extends Mandate> getActiveMandatesForAccount(String accountId);

	Mandate createMandateForAccount(String accountId);
	
	void updateMandate(Mandate mandate);
	
	Mandate createMandateFromJson(String json) throws IOException;

}
