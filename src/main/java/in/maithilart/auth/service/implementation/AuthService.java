package in.maithilart.auth.service.implementation;

import in.maithilart.auth.service.IAuthService;
import in.maithilart.common.exception.MaithilException;
import io.jsonwebtoken.Claims;
import in.maithilart.auth.dto.LoginRequest;
import in.maithilart.auth.dto.LoginResponse;
import in.maithilart.auth.dto.RegisterRequest;
import in.maithilart.auth.dto.RegisterResponse;

import in.maithilart.auth.entity.User;
import in.maithilart.auth.exception.UserAlreadyExistsException;
import in.maithilart.auth.mapper.UserMapper;
import in.maithilart.auth.repository.UserRepository;
import in.maithilart.auth.security.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final RedisTemplate<String, String> redisTemplate;

	private static final int MAX_ATTEMPTS = 5;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			RefreshTokenService refreshTokenService, RedisTemplate<String, String> redisTemplate) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public RegisterResponse register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			log.warn("Registration attempt with existing email={}", request.getEmail());
			throw new UserAlreadyExistsException("Email " + request.getEmail() + " is already registered.");
		}
		String hashedPassword = passwordEncoder.encode(request.getPassword());
		User user = UserMapper.toUser(request, hashedPassword);

		User saved = userRepository.save(user);
		Map<String, Object> body = Map.of("email", request.getEmail(), "name", request.getFullName());

		log.info("User registered successfully userId={}", saved.getId());

		return UserMapper.toRegisterResponse(saved);
	}

	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

		if (!user.isEnabled()) {
			throw new IllegalStateException("Account disabled");
		}

		if (user.isAccountLocked()) {
			throw new IllegalStateException("Account locked");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			handleFailedAttempt(user);
			throw new IllegalArgumentException("Invalid credentials");
		}

		// success
		user.setFailedLoginAttempts(0);
		userRepository.save(user);

		Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

		String token = jwtService.generateToken(user.getId().toString(), user.getEmail(), user.getFullName(), roles);
		String refreshToken = refreshTokenService.create(user.getId()).getToken();
		Instant expiresAt = jwtService.getExpiryInstant(token);
		return new LoginResponse(token, refreshToken, expiresAt, roles);
	}

	private void handleFailedAttempt(User user) {
		int attempts = user.getFailedLoginAttempts() + 1;
		user.setFailedLoginAttempts(attempts);

		if (attempts >= MAX_ATTEMPTS) {
			user.setAccountLocked(true);
		}

		userRepository.save(user);
	}

	public void logout(String bearerToken) {
		// 1. "Bearer " prefix hatao
		String token = bearerToken.substring(7);

		// 2. Token se JTI (ID) aur Expiry nikalo
		String jti = jwtService.extractId(token);
		Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

		// 3. TTL calculate karo (Kitni der tak blacklist mein rakhna hai)
		long ttl = expiration.getTime() - System.currentTimeMillis();

		if (ttl > 0) {
			// 4. Redis mein entry: Key = JTI, Value = "revoked"
			// Key khud expire ho jayegi jab token naturally expire hoga
			try {

				redisTemplate.opsForValue().set(jti, "revoked", Duration.ofMillis(ttl));

			} catch (RedisConnectionFailureException ex) {
				throw new MaithilException("LOGOUT_FAILED",
						"Logout service temporarily unavailable. Please try again.");
			}
		}
	}

}
