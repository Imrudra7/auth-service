package in.maithilart.auth.service;

import java.util.UUID;

import in.maithilart.auth.entity.User;

public interface IUserService {

	User findById(UUID userId);

	User findByEmail(String email);

}
