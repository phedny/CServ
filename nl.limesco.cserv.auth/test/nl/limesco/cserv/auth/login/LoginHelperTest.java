package nl.limesco.cserv.auth.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.SortedMap;

import org.amdatu.auth.tokenprovider.TokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class LoginHelperTest {

	@Spy @InjectMocks private LoginHelperServiceImpl loginHelper;
	
	@Mock private UserAdmin userAdmin;
	
	@Mock private TokenProvider tokenProvider;
	
	@Mock private User testUser;
	
	@Before
	public void setUp() throws Exception {
		final Properties credentials = new Properties();
		credentials.put("password", "testPassword");
		when(testUser.getName()).thenReturn("testUser");
		when(testUser.getType()).thenReturn(Role.USER);
		when(testUser.getCredentials()).thenReturn(credentials);
		
		when(userAdmin.getRole("testUser")).thenReturn(testUser);
		
		when(tokenProvider.generateToken(any(SortedMap.class))).thenAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				return ((SortedMap<String, String>) invocation.getArguments()[0]).get(TokenProvider.USERNAME);
			}
		});
		when(tokenProvider.verifyToken(any(String.class))).thenAnswer(new Answer<SortedMap<String, String>>() {
			public SortedMap<String, String> answer(InvocationOnMock invocation) throws Throwable {
				final SortedMap<String, String> map = Maps.newTreeMap();
				map.put(TokenProvider.USERNAME, (String) invocation.getArguments()[0]);
				return map;
			}
		});
	}
	
	@Test
	public void nonExistingUserCanNotObtainToken() throws Exception {
		Optional<String> token = loginHelper.obtainToken("{\"username\":\"nonExistingUser\",\"password\":\"testPassword\"}");
		assertEquals(false, token.isPresent());
	}
	
	@Test
	public void existingUserCanNotObtainTokenWithIncorrectPassword() throws Exception {
		Optional<String> token = loginHelper.obtainToken("{\"username\":\"testUser\",\"password\":\"incorrectPassword\"}");
		assertEquals(false, token.isPresent());
	}

	@Test
	public void existingUserCanObtainToken() throws Exception {
		final Optional<String> token = loginHelper.obtainToken("{\"username\":\"testUser\",\"password\":\"testPassword\"}");
		assertTrue(token.isPresent());
	}
	
	@Test
	public void usernameCanBeObtainedFromToken() throws Exception {
		final Optional<String> token = loginHelper.obtainToken("{\"username\":\"testUser\",\"password\":\"testPassword\"}");
		final Optional<String> username = loginHelper.getUsername(token.get());
		assertTrue(token.isPresent());
		assertEquals("testUser", username.get());
	}
	
}
