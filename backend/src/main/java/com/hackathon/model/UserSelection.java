package com.hackathon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_selections_6702", uniqueConstraints = @UniqueConstraint(columnNames = "teamLeadEmail"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String userId; // Auto-generated, unique
    
    @Column(nullable = false)
    private String teamName;
    
    @Column(nullable = false)
    private String teamLeadName;
    
    @Column(nullable = false, unique = true)
    private String teamLeadEmail; // Must be unique and end with @htcinc.com
    
    @ManyToOne
    @JoinColumn(name = "problem_statement_id", nullable = false)
    private ProblemStatement problemStatement;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (userId == null) {
            // Generate userId from email: USER_YYYYMMDD_RANDOMNUMBER
            String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(createdAt);
            String randomNum = String.format("%05d", System.nanoTime() % 100000);
            userId = "USER_" + timestamp + "_" + randomNum;
        }
    }
}
