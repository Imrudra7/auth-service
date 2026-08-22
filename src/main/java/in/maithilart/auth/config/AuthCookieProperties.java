package in.maithilart.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieProperties {

    @Value("${auth.cookie.http-only:true}")
    private boolean httpOnly;

    @Value("${auth.cookie.secure:false}")
    private boolean secure;

    @Value("${auth.cookie.same-site:Lax}")
    private String sameSite;

    @Value("${auth.cookie.path:/}")
    private String path;

    @Value("${auth.cookie.access-token-max-age-seconds:900}")
    private long accessTokenMaxAgeSeconds;

    @Value("${auth.cookie.refresh-token-max-age-seconds:604800}")
    private long refreshTokenMaxAgeSeconds;

    public ResponseCookie accessToken(String value) {
        return create("accessToken", value, accessTokenMaxAgeSeconds);
    }

    public ResponseCookie refreshToken(String value) {
        return create("refreshToken", value, refreshTokenMaxAgeSeconds);
    }

    public ResponseCookie clearAccessToken() {
        return create("accessToken", "", 0);
    }

    public ResponseCookie clearRefreshToken() {
        return create("refreshToken", "", 0);
    }

    private ResponseCookie create(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(secure)
                .path(path)
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
    }
}
