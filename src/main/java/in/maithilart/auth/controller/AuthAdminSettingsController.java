package in.maithilart.auth.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.maithilart.auth.config.AuthCookieProperties;
import in.maithilart.auth.dto.SecuritySettingsResponse;
import in.maithilart.auth.repository.RefreshTokenRepository;
import in.maithilart.common.dto.MaithilResponse;
import in.maithilart.common.exception.MaithilException;
import in.maithilart.common.security.MaithilPrincipal;

@RestController
@RequestMapping("/auth/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AuthAdminSettingsController {

	private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

	private final AuthCookieProperties authCookieProperties;
	private final RefreshTokenRepository refreshTokenRepository;

	public AuthAdminSettingsController(AuthCookieProperties authCookieProperties,
			RefreshTokenRepository refreshTokenRepository) {
		this.authCookieProperties = authCookieProperties;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@GetMapping("/security")
	public ResponseEntity<MaithilResponse<SecuritySettingsResponse>> getSecuritySettings() {
		SecuritySettingsResponse response = new SecuritySettingsResponse();
		response.setCookieHttpOnly(authCookieProperties.isHttpOnly());
		response.setCookieSecure(authCookieProperties.isSecure());
		response.setCookieSameSite(authCookieProperties.getSameSite());
		response.setCookiePath(authCookieProperties.getPath());
		response.setAccessTokenMaxAgeSeconds(authCookieProperties.getAccessTokenMaxAgeSeconds());
		response.setRefreshTokenMaxAgeSeconds(authCookieProperties.getRefreshTokenMaxAgeSeconds());
		response.setMaxFailedLoginAttempts(MAX_FAILED_LOGIN_ATTEMPTS);

		return ResponseEntity.ok(MaithilResponse.success("SECURITY_SETTINGS_FETCHED",
				"Security settings fetched successfully", response));
	}

	@PostMapping("/logout-all-devices")
	public ResponseEntity<MaithilResponse<Void>> logoutAllDevices(Authentication authentication) {
		UUID userId = currentUserId(authentication);
		refreshTokenRepository.revokeAllByUserId(userId);
		return ResponseEntity.ok(MaithilResponse.success("ADMIN_SESSIONS_REVOKED",
				"Refresh sessions revoked successfully", null));
	}

	private UUID currentUserId(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof MaithilPrincipal principal
				&& principal.getUserId() != null) {
			return UUID.fromString(principal.getUserId());
		}
		throw new MaithilException("AUTH_PRINCIPAL_MISSING", "Current admin identity is not available.");
	}
}
