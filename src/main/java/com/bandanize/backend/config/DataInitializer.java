package com.bandanize.backend.config;

import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user
            if (userRepository.count() == 0) {
                UserModel user = new UserModel();
                user.setName("Raul Del Valle");
                user.setUsername("admin");
                user.setEmail("admin@bandanize.com");
                user.setCity("Tarifa");
                user.setHashedPassword(passwordEncoder.encode("admin"));
                user.setDisabled(false);
                userRepository.save(user);
                System.out.println("Usuario inicial creado: " + user.getUsername());
            }
        };
    }
}