package in.maithilart.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import in.maithilart.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUserId(UUID userId);
    @Transactional
	void deleteAllByUserId(UUID userId);
	
	

	List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);
	@Transactional
	@Modifying
	@Query("update RefreshToken r set r.revoked = true where r.userId = :userId")
	void revokeAllByUserId(UUID userId);


}
