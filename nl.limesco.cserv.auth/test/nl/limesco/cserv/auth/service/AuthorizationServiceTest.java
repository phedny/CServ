package nl.limesco.cserv.auth.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import nl.limesco.cserv.auth.api.AuthorizationException;
import nl.limesco.cserv.auth.api.AuthorizationService;
import nl.limesco.cserv.auth.api.Role;

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
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {

	@Spy @InjectMocks private AuthorizationServiceImpl authorizationService;
	
	@Mock private UserAdmin userAdmin;
	
	@Mock private TokenProvider tokenProvider;

	@Mock private User userUser;

	@Mock private User adminUser;
	
	@Mock private Group userGroup;
	
	@Mock private Group adminGroup;
	
	@Mock private HttpServletRequest userRequest;
	
	@Mock private HttpServletRequest adminRequest;
	
	@Mock private HttpServletRequest anonymousRequest;

	@Before
	public void setUp() throws Exception {
		final Properties credentials = new Properties();
		credentials.put("password", "testPassword");
		
		final Properties userProperties = new Properties();
		userProperties.put(AuthorizationService.ACCOUNT_ID_KEY, "userAccount");
		when(userUser.getName()).thenReturn("userUser");
		when(userUser.getType()).thenReturn(org.osgi.service.useradmin.Role.USER);
		when(userUser.getCredentials()).thenReturn(credentials);
		when(userUser.getProperties()).thenReturn(userProperties);
		
		final org.osgi.service.useradmin.Role[] userMembers = new org.osgi.service.useradmin.Role[] { userUser };
		when(userGroup.getName()).thenReturn("USER");
		when(userGroup.getType()).thenReturn(org.osgi.service.useradmin.Role.GROUP);
		when(userGroup.getMembers()).thenReturn(userMembers);
		
		final Properties adminProperties = new Properties();
		adminProperties.put(AuthorizationService.ACCOUNT_ID_KEY, "adminAccount");
		when(adminUser.getName()).thenReturn("adminUser");
		when(adminUser.getType()).thenReturn(org.osgi.service.useradmin.Role.USER);
		when(adminUser.getCredentials()).thenReturn(credentials);
		when(adminUser.getProperties()).thenReturn(adminProperties);
		
		final org.osgi.service.useradmin.Role[] adminMembers = new org.osgi.service.useradmin.Role[] { adminUser };
		when(adminGroup.getName()).thenReturn("ADMIN");
		when(adminGroup.getType()).thenReturn(org.osgi.service.useradmin.Role.GROUP);
		when(adminGroup.getMembers()).thenReturn(adminMembers);
		
		when(userAdmin.getRole("userUser")).thenReturn(userUser);
		when(userAdmin.getRole("adminUser")).thenReturn(adminUser);
		when(userAdmin.getRole("USER")).thenReturn(userGroup);
		when(userAdmin.getRole("ADMIN")).thenReturn(adminGroup);
		
		when(userRequest.getHeader("X-Limesco-Token")).thenReturn("userUser");
		when(adminRequest.getHeader("X-Limesco-Token")).thenReturn("adminUser");
		when(anonymousRequest.getHeader("X-Limesco-Token")).thenReturn(null);
		
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
	public void userRequestIsLoggedIn() {
		assertEquals(true, authorizationService.isLoggedIn(userRequest));
	}
	
	@Test
	public void adminRequestIsLoggedIn() {
		assertEquals(true, authorizationService.isLoggedIn(adminRequest));
	}
	
	@Test
	public void anonymousRequestIsNotLoggedIn() {
		assertEquals(false, authorizationService.isLoggedIn(anonymousRequest));
	}
	
	@Test
	public void userRequestPassesRequiredLoggedIn() {
		authorizationService.requiredLoggedInUsername(userRequest);
	}
	
	@Test
	public void adminRequestPassesRequiredLoggedIn() {
		authorizationService.requiredLoggedInUsername(adminRequest);
	}
	
	@Test(expected = AuthorizationException.class)
	public void anonymousRequestFailsRequiredLoggedIn() {
		authorizationService.requireLoggedIn(anonymousRequest);
	}
	
	@Test
	public void userRequestHasUserRole() {
		assertEquals(true, authorizationService.userHasRole(userRequest, Role.USER));
	}
	
	@Test
	public void userRequestHasNotAdminRole() {
		assertEquals(false, authorizationService.userHasRole(userRequest, Role.ADMIN));
	}
	
	@Test
	public void adminRequestHasNotUserRole() {
		assertEquals(false, authorizationService.userHasRole(adminRequest, Role.USER));
	}
	
	@Test
	public void adminRequestHasAdminRole() {
		assertEquals(true, authorizationService.userHasRole(adminRequest, Role.ADMIN));
	}

	@Test
	public void anonymousRequestHasNotUserRole() {
		assertEquals(false, authorizationService.userHasRole(anonymousRequest, Role.USER));
	}

	@Test
	public void anonymousRequestHasNotAdminRole() {
		assertEquals(false, authorizationService.userHasRole(anonymousRequest, Role.ADMIN));
	}

	@Test
	public void userRequestPassesRequiredUserRole() {
		authorizationService.requireUserRole(userRequest, Role.USER);
	}
	
	@Test(expected = AuthorizationException.class)
	public void userRequestFailsRequiredAdminRole() {
		authorizationService.requireUserRole(userRequest, Role.ADMIN);
	}
	
	@Test(expected = AuthorizationException.class)
	public void adminRequestFailsRequiredUserRole() {
		authorizationService.requireUserRole(adminRequest, Role.USER);
	}
	
	@Test
	public void adminRequestPassesRequiredAdminRole() {
		authorizationService.requireUserRole(adminRequest, Role.ADMIN);
	}

	@Test(expected = AuthorizationException.class)
	public void anonymousRequestFailsRequiredUserRole() {
		authorizationService.requireUserRole(anonymousRequest, Role.USER);
	}

	@Test(expected = AuthorizationException.class)
	public void anonymousRequestFailsRequiredAdminRole() {
		authorizationService.requireUserRole(anonymousRequest, Role.ADMIN);
	}

	@Test
	public void userRequestHasUsername() {
		assertEquals(Optional.of("userUser"), authorizationService.getLoggedInUsername(userRequest));
	}
	
	@Test
	public void adminRequestHasUsername() {
		assertEquals(Optional.of("adminUser"), authorizationService.getLoggedInUsername(adminRequest));
	}
	
	@Test
	public void anonymousRequestHasNoUsername() {
		assertEquals(false, authorizationService.getLoggedInUsername(anonymousRequest).isPresent());
	}
	
	@Test
	public void userRequestPassesRequiredUsername() {
		assertEquals("userUser", authorizationService.requiredLoggedInUsername(userRequest));
	}
	
	@Test
	public void adminRequestPassesRequiredUsername() {
		assertEquals("adminUser", authorizationService.requiredLoggedInUsername(adminRequest));
	}
	
	@Test(expected = AuthorizationException.class)
	public void anonymousRequestFailsRequiredUsername() {
		authorizationService.requiredLoggedInUsername(anonymousRequest);
	}

	@Test
	public void userRequestHasAccountId() {
		assertEquals(Optional.of("userAccount"), authorizationService.getAccountId(userRequest));
	}
	
	@Test
	public void adminRequestHasAccountId() {
		assertEquals(Optional.of("adminAccount"), authorizationService.getAccountId(adminRequest));
	}
	
	@Test
	public void anonymousRequestHasNoAccountId() {
		assertEquals(false, authorizationService.getAccountId(anonymousRequest).isPresent());
	}
	
	@Test
	public void userRequestPassesRequiredAccountId() {
		assertEquals("userAccount", authorizationService.requiredAccountId(userRequest));
	}
	
	@Test
	public void adminRequestPassesRequiredAccountId() {
		assertEquals("adminAccount", authorizationService.requiredAccountId(adminRequest));
	}
	
	@Test(expected = AuthorizationException.class)
	public void anonymousRequestFailsRequiredAccountId() {
		authorizationService.requiredAccountId(anonymousRequest);
	}
	
}
