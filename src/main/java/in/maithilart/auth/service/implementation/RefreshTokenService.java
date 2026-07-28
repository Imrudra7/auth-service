package in.maithilart.auth.service.implementation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import in.maithilart.auth.entity.RefreshToken;
import in.maithilart.auth.repository.RefreshTokenRepository;
import in.maithilart.auth.service.ITokenService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService implements ITokenService {

	 private static final Logger log =
	            LoggerFactory.getLogger(AuthService.class); 
	 
    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public RefreshToken create(UUID userId) {
    	log.debug("Creating new Refresh token with userId: {}",userId);
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        token.setRevoked(false);
        repository.deleteAllByUserId(userId);
        

        return repository.save(token);
    }

    @Override
    public RefreshToken validate(String tokenValue) {

    	log.debug("Validating the refresh token : {}",tokenValue);
        RefreshToken token = repository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    	log.debug("Token was not invalid. : {}",tokenValue);

        if (token.isRevoked()) {
        	// 🚨 REUSE ATTACK DETECTED
            repository
                    .revokeAllByUserId(token.getUserId());
            throw new RuntimeException("Refresh token revoked");
        }
    	log.debug("Token was not revoked : {}",tokenValue);

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }
    	log.debug("Success!! Token was not expired : {}",tokenValue);

        return token;
    }

    @Override
    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    @Transactional
    @Override
    public void revokeAll(UUID userId) {
        repository.deleteByUserId(userId);
    }
}
