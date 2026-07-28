package in.maithilart.auth.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import in.maithilart.auth.entity.User;
import in.maithilart.auth.exception.UserNotFoundException;
import in.maithilart.auth.repository.UserRepository;
import in.maithilart.auth.service.IUserService;
@Service
public class UserService implements IUserService{

	private final UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	
	@Override
	public User findById(UUID userId) {
	    return userRepository.findById(userId)
	        .orElseThrow(() ->
	            new UserNotFoundException(
	                "User not found with this user id: " + userId
	            )
	        );
	}

	@Override
	public User findByEmail(String email) {
	    return userRepository.findByEmail(email)
	        .orElseThrow(() ->
	            new UserNotFoundException(
	                "User not found with this email: " + email
	            )
	        );
	}

}
