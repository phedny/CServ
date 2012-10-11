package org.amdatu.legacy.tenantuseradmin;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/**
 * Simply wraps a contained {@link UserAdmin} implementation and delegates all
 * calls to that implementation.
 */
public class UserAdminWrapper implements UserAdmin {

	private volatile UserAdmin m_delegate;

	@Override
	public Role createRole(String name, int type) {
		return m_delegate.createRole(name, type);
	}

	@Override
	public Authorization getAuthorization(User user) {
		return m_delegate.getAuthorization(user);
	}

	@Override
	public Role getRole(String name) {
		return m_delegate.getRole(name);
	}

	@Override
	public Role[] getRoles(String filter) throws InvalidSyntaxException {
		return m_delegate.getRoles(filter);
	}

	@Override
	public User getUser(String key, String value) {
		return m_delegate.getUser(key, value);
	}

	@Override
	public boolean removeRole(String name) {
		return m_delegate.removeRole(name);
	}
}
