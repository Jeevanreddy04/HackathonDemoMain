package com.hackathon.controller;

import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.UserSelectionRequest;
import com.hackathon.model.UserSelection;
import com.hackathon.service.UserSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-selections")
@RequiredArgsConstructor
public class UserSelectionController {
    
    private final UserSelectionService userSelectionService;
    
    /**
     * Register a user with problem statement selection
     * Request body: {teamName, teamLeadName, teamLeadEmail (must end with @htcinc.com), problemStatementId}
     * userId is auto-generated
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserSelection>> selectProblem(@RequestBody UserSelectionRequest request) {
        try {
            // Validate input
            if (request.getTeamName() == null || request.getTeamName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Validation failed", null, "Team name is required"));
            }
            if (request.getTeamLeadName() == null || request.getTeamLeadName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Validation failed", null, "Team lead name is required"));
            }
            if (request.getTeamLeadEmail() == null || request.getTeamLeadEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Validation failed", null, "Team lead email is required"));
            }
            if (request.getProblemStatementId() == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Validation failed", null, "Problem statement must be selected"));
            }
            
            UserSelection selection = userSelectionService.selectProblem(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Registration successful! Your User ID: " + selection.getUserId(), selection));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, "Registration failed", null, e.getMessage()));
        }
    }
    
    /**
     * Get user selection by email
     */
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserSelection>> getUserSelection(@PathVariable String email) {
        UserSelection selection = userSelectionService.getUserSelection(email);
        if (selection == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "No selection found for this email", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Selection retrieved successfully", selection));
    }

    /**
     * Download detailed selection report for the submitted user.
     */
    @GetMapping("/download-report")
    public ResponseEntity<byte[]> downloadSelectionReport(@RequestParam String email) {
        try {
            byte[] pdfFile = userSelectionService.downloadSelectionReport(email);
            String filename = "selection_report_" + email.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(pdfFile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Remove user selection by email
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse<Void>> deselectProblem(@PathVariable String email) {
        userSelectionService.deselectProblem(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Selection removed successfully", null));
    }
}
