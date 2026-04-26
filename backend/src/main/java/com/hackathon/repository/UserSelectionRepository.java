package com.hackathon.repository;

import com.hackathon.model.UserSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSelectionRepository extends JpaRepository<UserSelection, Long> {
    // Find user by unique email
    Optional<UserSelection> findByTeamLeadEmail(String teamLeadEmail);
    
    // Find all selections for a problem
    List<UserSelection> findByProblemStatementId(Long problemStatementId);
}
