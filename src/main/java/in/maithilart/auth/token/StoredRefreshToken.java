package in.maithilart.auth.token;

import java.time.Instant;
import java.util.UUID;

public class StoredRefreshToken {

    private UUID userId;
    private Instant expiresAt;
    private boolean revoked;

    public StoredRefreshToken(UUID userId, Instant expiresAt, boolean revoked) {
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
