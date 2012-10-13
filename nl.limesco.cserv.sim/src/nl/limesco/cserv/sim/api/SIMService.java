package nl.limesco.cserv.sim.api;

import java.util.List;
import nl.limesco.cserv.account.api.Account;
import com.google.common.base.Optional;

public interface SIMService {
	Optional<? extends SIM> getSIMByIMSI(String imsi);
	List<SIM> getSIMsFromAccount(Account a);
	
	SIM registerSIM(String imsi, String puk);
	void updateSIM(SIM sim);
}
