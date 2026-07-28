package in.maithilart.auth.controller;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.maithilart.auth.entity.RefreshToken;
import in.maithilart.auth.entity.User;
import in.maithilart.auth.security.JwtService;
import in.maithilart.auth.service.IUserService;
import in.maithilart.auth.service.implementation.RefreshTokenService;
 
@RestController
@RequestMapping("/auth")
public class RefreshController {
    private static final Logger log = LoggerFactory.getLogger(RefreshController.class);

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final IUserService userService;

    public RefreshController(RefreshTokenService refreshTokenService,
                             JwtService jwtService, IUserService userService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/admin/refresh") 
    public ResponseEntity<?> adminRefresh(
            @CookieValue(name = "refreshToken", required = false) String oldRefreshToken) {
        return internalRefresh(oldRefreshToken, "ADMIN");
    }

    @PostMapping("/refresh") 
    public ResponseEntity<?> userRefresh(
            @CookieValue(name = "refreshToken", required = false) String oldRefreshToken) {
        return internalRefresh(oldRefreshToken, "USER");
    }

    private ResponseEntity<?> internalRefresh(String oldRefreshToken, String requiredRole) {
        if (oldRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        RefreshToken token = refreshTokenService.validate(oldRefreshToken);
        User user = userService.findById(token.getUserId());

        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        if (!roleNames.contains(requiredRole)) {
            refreshTokenService.revoke(token); 
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bhai, tumhara role badal gaya hai. Dubara login karo.");
        }

        refreshTokenService.revoke(token);
        String newRefreshTokenStr = refreshTokenService.create(user.getId()).getToken();
        String newAccessToken = jwtService.generateToken(user.getId().toString(), user.getEmail(),user.getFullName(), roleNames);

        
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
              .httpOnly(true)
              .secure(false) // Dev: false, Prod: true
              .path("/")
              .maxAge(15 * 60) // 15 Mins
              .sameSite("Lax")
              .build();

      ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshTokenStr)
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(7 * 24 * 60 * 60) // 7 Days
              .sameSite("Lax")
              .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("status", "success", "message", "Token Refreshed for " + requiredRole));
    }
}