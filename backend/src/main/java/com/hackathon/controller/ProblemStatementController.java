package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.ProblemStatementDTO;
import com.hackathon.model.ProblemStatement;
import com.hackathon.service.ProblemStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problem-statements")
@RequiredArgsConstructor
public class ProblemStatementController {
    
    private final ProblemStatementService problemStatementService;
    
    @GetMapping
    public ResponseEntity<Page<ProblemStatementDTO>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProblemStatementDTO> problems = problemStatementService.getAllProblemStatements(pageable);
        
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProblemStatementDTO> getProblemById(@PathVariable Long id) {
        ProblemStatement problem = problemStatementService.getProblemStatementById(id);
        ProblemStatementDTO dto = new ProblemStatementDTO();
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setSelectionCount(problem.getCurrentUsers());
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createProblem(@RequestBody ProblemStatement problem) {
        problemStatementService.createProblemStatement(problem);
        return ResponseEntity.ok(new ApiResponse<>(true, "Problem created successfully", null));
    }
}
