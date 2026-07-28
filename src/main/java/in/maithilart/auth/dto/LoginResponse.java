package in.maithilart.auth.dto;

import java.time.Instant;
import java.util.Set;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;          // 🔥 NEW
    private String tokenType = "Bearer";
    private Instant expiresAt;
    private Set<String> roles;

    public LoginResponse(String accessToken,
                         String refreshToken,
                         Instant expiresAt,
                         Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.roles = roles;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public Instant getExpiresAt() { return expiresAt; }
    public Set<String> getRoles() { return roles; }
}
