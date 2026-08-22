package in.maithilart.auth.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.maithilart.auth.config.AuthCookieProperties;
import in.maithilart.auth.dto.LogoutRequest;
import in.maithilart.auth.entity.RefreshToken;
import in.maithilart.auth.service.implementation.AuthService;
import in.maithilart.auth.service.implementation.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final AuthCookieProperties authCookieProperties;

    public LogoutController(RefreshTokenService refreshTokenService,
    		AuthService authService,
    		AuthCookieProperties authCookieProperties) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
        this.authCookieProperties = authCookieProperties;
    }

	
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        @CookieValue(name = "accessToken", required = false) String accessToken,
        @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // 1. Redis Blacklisting (Tumhara logic yahan fit hoga) 🔥
        if (accessToken != null) {
            // Tumhara logic 'Bearer ' prefix maang raha hai, toh hum prefix de denge
            authService.logout("Bearer " + accessToken); 
        }

        // 2. Refresh Token Revoke (DB se delete)
        if (refreshToken != null) {
            RefreshToken rt = refreshTokenService.validate(refreshToken);
            refreshTokenService.revoke(rt);
        }

        // 3. Browser se Cookies clear karo
        ResponseCookie clearAccess = authCookieProperties.clearAccessToken();
                 
        ResponseCookie clearRefresh = authCookieProperties.clearRefreshToken();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .body(Map.of("message", "Logged out & Token Blacklisted successfully"));
    }
}
