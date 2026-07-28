package in.maithilart.auth.config;

import in.maithilart.auth.entity.User;
import in.maithilart.auth.repository.UserRepository;
import in.maithilart.auth.entity.Role; // Apna sahi enum path check kar lena

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Taaki user aur roles ek saath save hon
    public void run(String... args) {
        String adminEmail = "rudra@gmail.com";

        // 1. Pehle check karo ki admin exists karta hai ya nahi
        if (!userRepository.existsByEmail(adminEmail)) {
            
            User admin = new User();
            admin.setFullName("Rudra : The Maithil Art Admin");
            admin.setEmail(adminEmail);
            
            // 2. Password: 'admin123' (Ise baad mein dashboard se change kar lena)
            admin.setPassword(passwordEncoder.encode("rudra123"));
            
            admin.setEnabled(true);
            admin.setAccountLocked(false);
            
            // 3. Roles: ADMIN aur USER dono de do (Safe side ke liye)
            admin.setRoles(Set.of(Role.ADMIN, Role.USER));

            userRepository.save(admin);
            
            System.out.println("----------------------------------------------");
            System.out.println("🚀 [INIT] DEFAULT ADMIN CREATED SUCCESSFULLY");
            System.out.println("📧 Email: " + adminEmail);            
            System.out.println("----------------------------------------------");
        } else {
            System.out.println("✅ [INIT] Admin already exists. No action taken.");
        }
    }
}