package in.maithilart.auth.service.implementation;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.maithilart.auth.dto.AdminUserCreateRequest;
import in.maithilart.auth.dto.AdminUserResponse;
import in.maithilart.auth.dto.AdminUserStatusRequest;
import in.maithilart.auth.entity.Role;
import in.maithilart.auth.entity.User;
import in.maithilart.auth.exception.UserAlreadyExistsException;
import in.maithilart.auth.exception.UserNotFoundException;
import in.maithilart.auth.repository.RefreshTokenRepository;
import in.maithilart.auth.repository.UserRepository;
import in.maithilart.auth.service.AdminUserService;
import in.maithilart.common.exception.MaithilException;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminUserServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AdminUserResponse> getAdmins() {
		return userRepository.findByRole(Role.ADMIN).stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public AdminUserResponse createAdmin(AdminUserCreateRequest request) {
		String email = request.getEmail().trim().toLowerCase();
		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("Email " + email + " is already registered.");
		}

		User user = new User();
		user.setEmail(email);
		user.setFullName(request.getFullName().trim());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEnabled(true);
		user.setAccountLocked(false);
		user.setFailedLoginAttempts(0);
		user.setTermsAcceptedAt(Instant.now());
		user.setRoles(Set.of(Role.ADMIN, Role.USER));
		return toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public AdminUserResponse updateAdminStatus(UUID userId, AdminUserStatusRequest request, String actorUserId) {
		if (actorUserId != null && actorUserId.equals(userId.toString()) && Boolean.FALSE.equals(request.getEnabled())) {
			throw new MaithilException("ADMIN_SELF_DISABLE_BLOCKED", "Current admin cannot disable their own account.");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with this user id: " + userId));

		if (!user.getRoles().contains(Role.ADMIN)) {
			throw new MaithilException("ADMIN_USER_REQUIRED", "Only admin accounts can be managed here.");
		}

		if (request.getEnabled() != null) {
			user.setEnabled(request.getEnabled());
		}
		if (request.getAccountLocked() != null) {
			user.setAccountLocked(request.getAccountLocked());
			if (!request.getAccountLocked()) {
				user.setFailedLoginAttempts(0);
			}
		}

		return toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public void deleteAdmin(UUID userId, String actorUserId) {
		if (actorUserId != null && actorUserId.equals(userId.toString())) {
			throw new MaithilException("ADMIN_SELF_DELETE_BLOCKED", "Current admin cannot delete their own account.");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with this user id: " + userId));

		if (!user.getRoles().contains(Role.ADMIN)) {
			throw new MaithilException("ADMIN_USER_REQUIRED", "Only admin accounts can be managed here.");
		}

		refreshTokenRepository.deleteAllByUserId(userId);
		userRepository.delete(user);
	}

	private AdminUserResponse toResponse(User user) {
		AdminUserResponse response = new AdminUserResponse();
		response.setUserId(user.getId());
		response.setEmail(user.getEmail());
		response.setFullName(user.getFullName());
		response.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
		response.setEnabled(user.isEnabled());
		response.setAccountLocked(user.isAccountLocked());
		response.setFailedLoginAttempts(user.getFailedLoginAttempts());
		response.setCreatedAt(user.getCreatedAt());
		response.setUpdatedAt(user.getUpdatedAt());
		return response;
	}
}
