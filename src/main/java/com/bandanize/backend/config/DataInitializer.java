package com.bandanize.backend.config;

import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Create admin user
            if (userRepository.count() == 0) {
                UserModel user = new UserModel();
                user.setName("Admin User");
                user.setEmail("admin@bandanize.com");
                userRepository.save(user);
                System.out.println("Usuario inicial creado: " + user.getName());
            }
        };
    }
}