package com.hackathon.service;

import com.hackathon.dto.ProblemStatementDTO;
import com.hackathon.model.ProblemStatement;
import com.hackathon.repository.ProblemStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemStatementService {
    
    private final ProblemStatementRepository problemStatementRepository;
    
    public Page<ProblemStatementDTO> getAllProblemStatements(Pageable pageable) {
        Page<ProblemStatement> problems = problemStatementRepository.findAll(pageable);
        
        return problems.map(problem -> {
            ProblemStatementDTO dto = new ProblemStatementDTO();
            dto.setId(problem.getId());
            dto.setTitle(problem.getTitle());
            dto.setDescription(problem.getDescription());
            dto.setSelectionCount(problem.getCurrentUsers());
            
            return dto;
        });
    }
    
    public ProblemStatement getProblemStatementById(Long id) {
        return problemStatementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem Statement not found"));
    }
    
    public void createProblemStatement(ProblemStatement problemStatement) {
        problemStatement.setCurrentUsers(0);
        problemStatement.setMaxUsers(2);
        problemStatement.setIsAvailable(true);
        problemStatementRepository.save(problemStatement);
    }
    
    public void updateUserCount(Long problemStatementId, int change) {
        ProblemStatement problem = getProblemStatementById(problemStatementId);
        problem.setCurrentUsers(problem.getCurrentUsers() + change);
        problem.setIsAvailable(problem.getCurrentUsers() < problem.getMaxUsers());
        problemStatementRepository.save(problem);
    }
    
    public boolean canSelectProblem(Long problemStatementId) {
        ProblemStatement problem = getProblemStatementById(problemStatementId);
        return problem.getCurrentUsers() < problem.getMaxUsers();
    }
}
