package in.maithilart.auth.token;

import java.time.Instant;
import java.util.*;

public class RefreshTokenStore {

    private static final Map<String, StoredRefreshToken> STORE = new HashMap<>();

    public static void save(String token, UUID userId, Instant expiresAt) {
        STORE.put(token, new StoredRefreshToken(userId, expiresAt, false));
    }

    public static StoredRefreshToken get(String token) {
        return STORE.get(token);
    }

    public static void revoke(String token) {
        StoredRefreshToken stored = STORE.get(token);
        if (stored != null) {
            stored.setRevoked(true);
        }
    }

    public static void delete(String token) {
        STORE.remove(token);
    }
}
