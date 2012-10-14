package nl.limesco.cserv.sim.api;

import java.util.Collection;

import com.google.common.base.Optional;

public interface SimService {
	Optional<? extends Sim> getSimByIccid(String iccid);
	Collection<? extends Sim> getSimsByOwnerAccountId(String accountId);
	
	Sim registerSim(String iccid, String puk);
	void updateSim(Sim sim);
}
