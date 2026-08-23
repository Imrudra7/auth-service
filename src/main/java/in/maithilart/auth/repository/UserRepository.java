package in.maithilart.auth.repository;

import in.maithilart.auth.entity.User;
import in.maithilart.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("select distinct u from User u join u.roles r where r = :role order by u.createdAt desc")
    List<User> findByRole(Role role);
}
