package com.hackathon.config;

import com.hackathon.model.Admin;
import com.hackathon.model.ProblemStatement;
import com.hackathon.repository.AdminRepository;
import com.hackathon.repository.ProblemStatementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initDatabase(ProblemStatementRepository problemRepository,
                                         AdminRepository adminRepository) {
        return args -> {
            // Create default admin user if not exists
            Optional<Admin> existingAdmin = adminRepository.findByUsername("admin");
            if (existingAdmin.isEmpty()) {
                Admin defaultAdmin = new Admin();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setPassword("admin@123");
                defaultAdmin.setEmail("admin@hackathon.com");
                defaultAdmin.setFullName("Admin User");
                defaultAdmin.setIsActive(true);
                defaultAdmin.setCreatedAt(LocalDateTime.now());
                adminRepository.save(defaultAdmin);
            }
        };
    }
}
