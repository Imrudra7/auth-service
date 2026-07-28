package in.maithilart.auth.dto;

import java.util.Set;
import java.util.UUID;

public class RegisterResponse {

    private UUID userId;
    private String email;
    private String fullName;
    private Set<String> roles;

    public RegisterResponse(UUID userId, String email, String fullName, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    // getters
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Set<String> getRoles() { return roles; }
}
