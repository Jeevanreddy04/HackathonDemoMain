package com.hackathon.repository;

import com.hackathon.model.ProblemStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemStatementRepository extends JpaRepository<ProblemStatement, Long> {
    Page<ProblemStatement> findAll(Pageable pageable);
    Optional<ProblemStatement> findByProblemId(String problemId);
}
