package com.hackathon.service;

import com.hackathon.dto.AdminLoginResponse;
import com.hackathon.model.Admin;
import com.hackathon.model.ProblemStatement;
import com.hackathon.model.UserSelection;
import com.hackathon.repository.AdminRepository;
import com.hackathon.repository.ProblemStatementRepository;
import com.hackathon.repository.UserSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final int MAX_USERS_PER_PROBLEM = 2;
    
    private final AdminRepository adminRepository;
    private final ProblemStatementRepository problemStatementRepository;
    private final UserSelectionRepository userSelectionRepository;
    
    /**
     * Authenticate admin login
     */
    public AdminLoginResponse authenticateAdmin(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsernameAndPassword(username, password);
        
        if (admin.isPresent()) {
            Admin adminUser = admin.get();
            if (!adminUser.getIsActive()) {
                return new AdminLoginResponse(null, null, null, null, 
                    "Admin account is inactive", false);
            }
            
            // Update last login time
            adminUser.setLastLoginAt(LocalDateTime.now());
            adminRepository.save(adminUser);
            
            return new AdminLoginResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getFullName(),
                adminUser.getEmail(),
                "Login successful",
                true
            );
        }
        
        return new AdminLoginResponse(null, null, null, null, 
            "Invalid username or password", false);
    }
    
    /**
     * Upload problem statements from Excel file
     * Expected format: Column A (Problem ID), Column B (Title), Column C (Description)
     * Column D (Max Users), if present, is ignored because each statement is limited to 2 users.
     */
    public String uploadProblemStatementsFromExcel(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return "File is empty";
        }
        
        try (InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = getProblemStatementSheet(workbook);
            Iterator<Row> rows = sheet.iterator();
            
            int rowCount = 0;
            int successCount = 0;
            
            if (!rows.hasNext()) {
                return "Successfully uploaded 0 problem statements out of 0 rows";
            }
            
            Row firstRow = rows.next();
            if (firstRow != null && !isRowBlank(firstRow)) {
                if (!isHeaderRow(firstRow)) {
                    rowCount++;
                    if (processExcelRow(firstRow, problemStatementRepository)) {
                        successCount++;
                    }
                }
            }
            
            // Process remaining rows
            while (rows.hasNext()) {
                Row row = rows.next();
                if (row == null || isRowBlank(row)) {
                    continue;
                }
                
                rowCount++;
                
                try {
                    if (processExcelRow(row, problemStatementRepository)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    // Log error and continue with next row
                    System.err.println("Error processing row " + rowCount + ": " + e.getMessage());
                }
            }
            
            return String.format("Successfully uploaded %d problem statements out of %d rows", 
                successCount, rowCount);
            
        } catch (Exception e) {
            return "Error uploading file: " + e.getMessage();
        }
    }
    
    /**
     * Helper method to process a single Excel row
     */
    private boolean processExcelRow(Row row, ProblemStatementRepository repository) {
        try {
            String problemId = getCellValue(row.getCell(0));
            String title = getCellValue(row.getCell(1));
            String description = getCellValue(row.getCell(2));
            
            if (problemId == null || problemId.trim().isEmpty() ||
                title == null || title.trim().isEmpty() ||
                description == null || description.trim().isEmpty()) {
                return false;
            }
            
            String trimmedProblemId = problemId.trim();
            Optional<ProblemStatement> existingProblem = repository.findByProblemId(trimmedProblemId);
            
            ProblemStatement problem;
            if (existingProblem.isPresent()) {
                problem = existingProblem.get();
            } else {
                problem = new ProblemStatement();
                problem.setProblemId(trimmedProblemId);
                problem.setCurrentUsers(0);
            }
            
            problem.setTitle(title.trim());
            problem.setDescription(description.trim());
            problem.setMaxUsers(MAX_USERS_PER_PROBLEM);
            problem.setIsAvailable(problem.getCurrentUsers() < MAX_USERS_PER_PROBLEM);
            repository.save(problem);
            return true;
        } catch (Exception e) {
            System.err.println("Error processing row: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Download all problem statements as Excel file
     */
    public byte[] downloadProblemStatementsAsExcel() throws Exception {
        List<ProblemStatement> problems = problemStatementRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Problem Statements");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Problem ID", "Title", "Description", "Max Users", "Current Users", "Available"};
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Populate data rows
            int rowNum = 1;
            for (ProblemStatement problem : problems) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(problem.getProblemId());
                row.createCell(1).setCellValue(problem.getTitle());
                row.createCell(2).setCellValue(problem.getDescription());
                row.createCell(3).setCellValue(problem.getMaxUsers());
                row.createCell(4).setCellValue(problem.getCurrentUsers());
                row.createCell(5).setCellValue(problem.getIsAvailable() ? "Yes" : "No");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        }
    }
    
    /**
     * Download all user selections as Excel file
     */
    public byte[] downloadUserSelectionsAsExcel() throws Exception {
        List<UserSelection> selections = userSelectionRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("User Selections");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"User ID", "Team Name", "Team Lead Name", "Team Lead Email", 
                "Problem Statement", "Selected Date"};
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Populate data rows
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (UserSelection selection : selections) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(selection.getUserId());
                row.createCell(1).setCellValue(selection.getTeamName());
                row.createCell(2).setCellValue(selection.getTeamLeadName());
                row.createCell(3).setCellValue(selection.getTeamLeadEmail());
                row.createCell(4).setCellValue(selection.getProblemStatement().getTitle());
                row.createCell(5).setCellValue(selection.getCreatedAt().format(formatter));
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        }
    }
    
    /**
     * Download combined summary as Excel file (both problem statements and user selections)
     */
    public byte[] downloadCombinedReportAsExcel() throws Exception {
        List<ProblemStatement> problems = problemStatementRepository.findAll();
        List<UserSelection> selections = userSelectionRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Sheet 1: Problem Statements
            createProblemStatementsSheet(workbook, problems);
            
            // Sheet 2: User Selections
            createUserSelectionsSheet(workbook, selections);
            
            // Sheet 3: Summary
            createSummarySheet(workbook, problems, selections);
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void createProblemStatementsSheet(Workbook workbook, List<ProblemStatement> problems) {
        Sheet sheet = workbook.createSheet("Problem Statements");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Problem ID", "Title", "Description", "Max Users", "Current Users", "Available"};
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (ProblemStatement problem : problems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(problem.getProblemId());
            row.createCell(1).setCellValue(problem.getTitle());
            row.createCell(2).setCellValue(problem.getDescription());
            row.createCell(3).setCellValue(problem.getMaxUsers());
            row.createCell(4).setCellValue(problem.getCurrentUsers());
            row.createCell(5).setCellValue(problem.getIsAvailable() ? "Yes" : "No");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createUserSelectionsSheet(Workbook workbook, List<UserSelection> selections) {
        Sheet sheet = workbook.createSheet("User Selections");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"User ID", "Team Name", "Team Lead Name", "Team Lead Email", 
            "Problem Statement", "Selected Date"};
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (UserSelection selection : selections) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(selection.getUserId());
            row.createCell(1).setCellValue(selection.getTeamName());
            row.createCell(2).setCellValue(selection.getTeamLeadName());
            row.createCell(3).setCellValue(selection.getTeamLeadEmail());
            row.createCell(4).setCellValue(selection.getProblemStatement().getTitle());
            row.createCell(5).setCellValue(selection.getCreatedAt().format(formatter));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createSummarySheet(Workbook workbook, List<ProblemStatement> problems, 
                                   List<UserSelection> selections) {
        Sheet sheet = workbook.createSheet("Summary");
        
        CellStyle titleStyle = createTitleStyle(workbook);
        
        Row row1 = sheet.createRow(0);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("Hackathon Form Summary Report");
        cell1.setCellStyle(titleStyle);
        
        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("Generated on: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        Row row4 = sheet.createRow(3);
        row4.createCell(0).setCellValue("Total Problem Statements:");
        row4.createCell(1).setCellValue(problems.size());
        
        Row row5 = sheet.createRow(4);
        row5.createCell(0).setCellValue("Total Team Registrations:");
        row5.createCell(1).setCellValue(selections.size());
        
        Row row6 = sheet.createRow(5);
        row6.createCell(0).setCellValue("Available Problem Statements:");
        row6.createCell(1).setCellValue(problems.stream().filter(ProblemStatement::getIsAvailable).count());
        
        Row row7 = sheet.createRow(6);
        row7.createCell(0).setCellValue("Fully Booked Problems:");
        row7.createCell(1).setCellValue(problems.stream()
            .filter(p -> !p.getIsAvailable()).count());
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return value == null || value.trim().isEmpty() ? null : value;
    }
    
    private Sheet getProblemStatementSheet(Workbook workbook) {
        Sheet shortlistedSheet = workbook.getSheet("Shortlisted");
        if (shortlistedSheet != null) {
            return shortlistedSheet;
        }
        
        return workbook.getSheetAt(0);
    }
    
    private boolean isHeaderRow(Row row) {
        String firstCell = getCellValue(row.getCell(0));
        String secondCell = getCellValue(row.getCell(1));
        
        if (firstCell == null) {
            return true;
        }
        
        String first = firstCell.trim().toLowerCase();
        String second = secondCell == null ? "" : secondCell.trim().toLowerCase();
        return first.contains("problem") && (first.contains("id") || second.contains("title"));
    }
    
    private boolean isRowBlank(Row row) {
        for (int i = 0; i <= 2; i++) {
            if (getCellValue(row.getCell(i)) != null) {
                return false;
            }
        }
        return true;
    }
}
