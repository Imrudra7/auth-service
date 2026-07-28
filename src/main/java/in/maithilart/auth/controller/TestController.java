package in.maithilart.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.maithilart.auth.utils.AuthCurrentUserProvider;
import in.maithilart.common.security.MaithilPrincipal;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth/api")
public class TestController {
	private final AuthCurrentUserProvider currentUserProvider;

	public TestController(AuthCurrentUserProvider currentUserProvider) {
		this.currentUserProvider = currentUserProvider;

	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String userApi() {
		return "Hello USER";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminApi() {
		return "Hello ADMIN";
	}

	@GetMapping("/common")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String commonApi() {
		return "Hello USER or ADMIN";
	}

	@GetMapping("/me")
	public String me() {
		MaithilPrincipal principal = currentUserProvider.getCurrentUser();
		return principal.getEmail(); // userId (JWT subject)
	}
}
