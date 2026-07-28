package in.maithilart.auth.mapper;

import in.maithilart.auth.dto.RegisterRequest;
import in.maithilart.auth.dto.RegisterResponse;
import in.maithilart.auth.entity.Role;
import in.maithilart.auth.entity.User;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {} // utility class

    public static User toUser(RegisterRequest request, String hashedPassword) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(hashedPassword);
        user.setRoles(Set.of(Role.USER));
        user.setEnabled(true);
        user.setTermsAcceptedAt(Instant.now());
        return user;
    }

    public static RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles()
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet())
        );
    }
}
