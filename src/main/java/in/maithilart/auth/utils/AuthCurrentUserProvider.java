package in.maithilart.auth.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import in.maithilart.common.context.provider.CurrentUserProvider;
import in.maithilart.common.security.MaithilPrincipal;

@Component
public class AuthCurrentUserProvider implements CurrentUserProvider {

	@Override
	public MaithilPrincipal getCurrentUser() {

		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof MaithilPrincipal maithilPrincipal) {
			return maithilPrincipal;
		}

		return null;
	}

}
