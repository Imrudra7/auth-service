package in.maithilart.auth.service;

import java.util.UUID;

import in.maithilart.auth.entity.RefreshToken;

public interface ITokenService {

	RefreshToken create(UUID userId);

	RefreshToken validate(String tokenValue);

	void revoke(RefreshToken token);

	void revokeAll(UUID userId);

	

}
