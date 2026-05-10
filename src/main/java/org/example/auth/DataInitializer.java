package org.example.auth;

import lombok.RequiredArgsConstructor;
import org.example.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("admin").ifPresent(user -> {
            if (!passwordEncoder.matches("admin", user.getPassword())) {
                user.setPassword(passwordEncoder.encode("admin"));
                userRepository.save(user);
            }
        });
    }
}
