package com.hackathon.controller;

import com.hackathon.dto.AdminLoginRequest;
import com.hackathon.dto.AdminLoginResponse;
import com.hackathon.dto.ApiResponse;
import com.hackathon.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    /**
     * Admin login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.authenticateAdmin(request.getUsername(), request.getPassword());
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Upload problem statements from Excel file
     */
    @PostMapping("/upload-problems")
    public ResponseEntity<ApiResponse<String>> uploadProblems(@RequestParam("file") MultipartFile file) {
        try {
            String result = adminService.uploadProblemStatementsFromExcel(file);
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Error uploading file: " + e.getMessage(), null));
        }
    }
    
    /**
     * Download problem statements as Excel file
     */
    @GetMapping("/download-problems")
    public ResponseEntity<byte[]> downloadProblems() {
        try {
            byte[] excelFile = adminService.downloadProblemStatementsAsExcel();
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=problem_statements.xlsx")
                .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Download user selections as Excel file
     */
    @GetMapping("/download-selections")
    public ResponseEntity<byte[]> downloadSelections() {
        try {
            byte[] excelFile = adminService.downloadUserSelectionsAsExcel();
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_selections.xlsx")
                .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Download combined report (all details) as Excel file
     */
    @GetMapping("/download-combined-report")
    public ResponseEntity<byte[]> downloadCombinedReport() {
        try {
            byte[] excelFile = adminService.downloadCombinedReportAsExcel();
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hackathon_complete_report.xlsx")
                .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
