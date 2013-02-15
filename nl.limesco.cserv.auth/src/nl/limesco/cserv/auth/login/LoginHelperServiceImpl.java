package nl.limesco.cserv.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Random;
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
import com.google.common.hash.Hashing;

public class LoginHelperServiceImpl implements LoginHelperService {

	public static final String HASH_METHOD = "sha256salt";
	
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
		
		final Role role;
		try {
			role = userAdmin.getRole(username);
		} catch(ClassCastException e) {
			System.out.println("Class cast exception in retrieving user role: see CServ Bug #77 to fix your database");
			return Optional.absent();
		}
		
		if (role == null || role.getType() != Role.USER) {
			return Optional.absent();
		}
		
		final User user = (User) role;
		final String actualPassword = (String) user.getCredentials().get("password");
		final String hashedPassword = (String) user.getCredentials().get("hashedpassword");
		
		boolean passwordOk = false;
		if(hashedPassword != null && verifyHashedPassword(hashedPassword, password)) {
			passwordOk = true;
		}
		
		if(actualPassword != null && password.equals(actualPassword)) {
			passwordOk = true;
			// Transition from plain-text passwords to hashed passwords
			user.getCredentials().put("hashedpassword", generateHashedPassword(actualPassword));
			user.getCredentials().remove("password");
			assert(verifyHashedPassword((String)user.getCredentials().get("hashedpassword"), password));
		}
		
		if(!passwordOk) {
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
	
	private String generateHashedPassword(String password, String salt) {
		return Hashing.sha256().hashString(password + salt).toString();
	}
	
	@Override
	public String generateHashedPassword(String password) {
		assert(password.length() >= 4);
		String salt = "";
		final Random random = new Random();
		for(int i = 0; i < 20; ++i) {
			int n = random.nextInt(62);
			if(n < 10)      salt += (char) (0x30 + n);      // numerics
			else if(n < 36) salt += (char) (0x41 + n - 10); // uppercase
			else            salt += (char) (0x61 + n - 36); // lowercase
		}
		final String hashed = generateHashedPassword(password, salt);
		return HASH_METHOD + "!" + salt + "!" + hashed;
	}

	@Override
	public boolean verifyHashedPassword(String hash, String password) {
		if(hash == null) return false;
		
		final int firstM = hash.indexOf('!');
		if(firstM == -1) return false;
		if(firstM == hash.length()) return false;
		final int secondM = hash.indexOf('!', firstM + 1);
		if(secondM == -1) return false;
		
		final String method    = hash.substring(0, firstM);
		if(!method.equals(HASH_METHOD)) {
			System.out.println("Database salting method not understood: " + method);
			return false;
		}
		final String salt      = hash.substring(firstM + 1, secondM);
		final String realHash  = hash.substring(secondM + 1);
		final String checkHash = generateHashedPassword(password, salt);
		return checkHash.equals(realHash);
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
