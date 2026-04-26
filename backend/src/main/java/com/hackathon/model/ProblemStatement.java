package com.hackathon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problem_statements_6702")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemStatement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String problemId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    // @Column(nullable = false)
    // private String category;
    
    // @Column(nullable = false)
    // private String difficulty;
    
    @Column(nullable = false)
    private Integer maxUsers;
    
    @Column(nullable = false)
    private Integer currentUsers;
    
    @Column(nullable = false)
    private Boolean isAvailable;
}
