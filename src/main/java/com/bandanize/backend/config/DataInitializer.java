package com.bandanize.backend.config;

import com.bandanize.backend.models.UserModel;
import com.bandanize.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Create admin user
            if (userRepository.count() == 0) {
                UserModel user = new UserModel();
                user.setName("");
                user.setUsername("admin");
                user.setFullName("Administrator");
                user.setEmail("admin@bandanize.com");
                user.setCity("Headquarters");
                user.setHashedPassword("securepassword");
                user.setDisabled(false);
                user.setPhoto("https://example.com/admin-photo.png");
                user.setRrss(Arrays.asList("https://twitter.com/admin", "https://facebook.com/admin"));
                userRepository.save(user);
                System.out.println("Usuario inicial creado: " + user.getUsername());
            }
        };
    }
}