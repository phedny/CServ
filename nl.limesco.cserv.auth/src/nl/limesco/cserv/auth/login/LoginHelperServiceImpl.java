package nl.limesco.cserv.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.SortedMap;

import org.amdatu.auth.tokenprovider.InvalidTokenException;
import org.amdatu.auth.tokenprovider.TokenProvider;
import org.amdatu.auth.tokenprovider.TokenProviderException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class LoginHelperServiceImpl implements LoginHelperService {

	private volatile UserAdmin userAdmin;
	
	private volatile TokenProvider tokenProvider;

	@Override
	public Optional<String> obtainToken(String json) throws JsonParseException, JsonMappingException, IOException, TokenProviderException {
		checkNotNull(json);
		
		final LoginParameters parameters = new ObjectMapper().readValue(json, LoginParameters.class);
		return obtainToken(parameters.getUsername(), parameters.getPassword());
	}

	@Override
	public Optional<String> obtainToken(String username, String password) throws TokenProviderException {
		checkNotNull(username);
		checkNotNull(password);
		
		final Role role = userAdmin.getRole(username);
		if (role == null || role.getType() != Role.USER) {
			return Optional.absent();
		}
		
		final User user = (User) role;
		final String actualPassword = (String) user.getCredentials().get("password");
		if (!password.equals(actualPassword)) {
			return Optional.absent();
		}
		
		final SortedMap<String, String> tokenProperties = Maps.newTreeMap();
		tokenProperties.put(TokenProvider.USERNAME, user.getName());
		return Optional.of(tokenProvider.generateToken(tokenProperties));
	}

	@Override
	public Optional<String> getUsername(String token) throws TokenProviderException, InvalidTokenException {
		final SortedMap<String, String> tokenProperties = tokenProvider.verifyToken(token);
		if (tokenProperties != null) {
			return Optional.of(tokenProperties.get(TokenProvider.USERNAME));
		} else {
			return Optional.absent();
		}
	}

	private static final class LoginParameters {
		
		private String username;
		
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
	}
	
}
