package nl.limesco.cserv.ideal.targetpay;

import java.io.IOException;
import java.net.URL;

public interface IdealHttpTool {

	String doIdeal(URL url, String parameters) throws IOException;
	
}
