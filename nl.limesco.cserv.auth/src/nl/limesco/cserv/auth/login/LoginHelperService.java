package nl.limesco.cserv.auth.login;

import java.io.IOException;

import org.amdatu.auth.tokenprovider.InvalidTokenException;
import org.amdatu.auth.tokenprovider.TokenProviderException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.common.base.Optional;

public interface LoginHelperService {

	Optional<String> obtainToken(String json) throws JsonParseException, JsonMappingException, IOException, TokenProviderException;
	
	Optional<String> obtainToken(String username, String password) throws TokenProviderException;

	Optional<String> getUsername(String token) throws TokenProviderException, InvalidTokenException;

}
