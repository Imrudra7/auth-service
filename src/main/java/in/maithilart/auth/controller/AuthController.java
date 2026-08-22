package in.maithilart.auth.controller;

import in.maithilart.auth.dto.LoginRequest;
import in.maithilart.auth.dto.LoginResponse;
import in.maithilart.auth.dto.RegisterRequest;
import in.maithilart.auth.dto.RegisterResponse;
import in.maithilart.auth.entity.User;
import in.maithilart.auth.service.IAuthService;
import in.maithilart.auth.service.IUserService;
import in.maithilart.common.annotation.Audit;

import in.maithilart.common.annotation.PublishMaithilEvent;
import in.maithilart.common.constants.MaithilConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import in.maithilart.auth.config.AuthCookieProperties;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	private final IAuthService authService;
	private final IUserService userService;
	private final AuthCookieProperties authCookieProperties;

	public AuthController(IAuthService authService, IUserService userService, AuthCookieProperties authCookieProperties) {
		this.authService = authService;
		this.userService = userService;
		this.authCookieProperties = authCookieProperties;
	}

	@PublishMaithilEvent(eventType = MaithilConstants.USER_CREATED, entityType = "USER", entityIdField = "userId")
	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

		RegisterResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	@PostMapping("/login")
	public ResponseEntity<?> userLogin(@RequestBody LoginRequest request) {
		log.debug("User login attempt");
		return internalLogin(request, "USER");
	}

	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
		log.debug("Admin login attempt");
		return internalLogin(request, "ADMIN");
	}

	private ResponseEntity<?> internalLogin(LoginRequest request, String requiredRole) {
		log.debug("Processing login for role={}", requiredRole);

		LoginResponse response = authService.login(request);
		log.debug("Login roles resolved for role={}", requiredRole);
		Set<String> roles = response.getRoles();

		if (!roles.contains(requiredRole)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "ACCESS_DENIED", "message",
					"Bhai, is area mein " + requiredRole + " hi allowed hain!"));
		}
		ResponseCookie accessCookie = authCookieProperties.accessToken(response.getAccessToken());
		ResponseCookie refreshCookie = authCookieProperties.refreshToken(response.getRefreshToken());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(Map.of("status", "success", "roles", roles, "message", "Login Successful"));

	}

	@GetMapping("/api/secure")
	public String secure() {
		return "You are authenticated";
	}

	@Audit(action = "USER_PROFILE_FETCHED", entityType = "USER", entityIdField = "id")
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
		String userId = request.getHeader("X-User-Id");

		// 2. DB se user ki fresh details lana
		User user = userService.findById(UUID.fromString(userId));

		// 3. Response bhejo (Same as LoginResponse format)
		Map<String, Object> response = new HashMap<>();
		response.put("id", user.getId());
		response.put("fullName", user.getFullName());
		response.put("email", user.getEmail());
		response.put("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

		return ResponseEntity.ok(response);
	}

	@Audit(action = "TEST_FAILURE", entityType = "TEST")
	@GetMapping("/test-failure")
	public String testFailure() {

		throw new RuntimeException("Boom");
	}

}
