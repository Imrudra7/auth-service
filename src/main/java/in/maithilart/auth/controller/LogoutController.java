package in.maithilart.auth.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.maithilart.auth.dto.LogoutRequest;
import in.maithilart.auth.entity.RefreshToken;
import in.maithilart.auth.service.implementation.AuthService;
import in.maithilart.auth.service.implementation.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public LogoutController(RefreshTokenService refreshTokenService,
    		AuthService authService) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
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
        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true).path("/").maxAge(0).build();
                
        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).path("/").maxAge(0).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .body(Map.of("message", "Logged out & Token Blacklisted successfully"));
    }
}
