package nl.limesco.cserv.sim.api;

import java.util.Calendar;
import java.util.Collection;

import com.google.common.base.Optional;

public interface SimService {
	Optional<? extends Sim> getSimByIccid(String iccid);
	Collection<? extends Sim> getSimsByOwnerAccountId(String accountId);
	Collection<? extends Sim> getActivatedSimsWithoutActivationInvoice();
	/**
	 * @param month Date object, of which only year and month are used.
	 * @return a collection of sims whose monthly fees were last invoiced before the
	 * month in the given parameter.
	 */
	Collection<? extends Sim> getActivatedSimsLastInvoicedBefore(Calendar month);
	
	Sim registerSim(String iccid, String puk);
	void updateSim(Sim sim);
}
