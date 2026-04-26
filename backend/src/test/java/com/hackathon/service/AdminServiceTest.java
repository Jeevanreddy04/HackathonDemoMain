package com.hackathon.service;

import com.hackathon.model.ProblemStatement;
import com.hackathon.repository.AdminRepository;
import com.hackathon.repository.ProblemStatementRepository;
import com.hackathon.repository.UserSelectionRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    private final AdminRepository adminRepository = mock(AdminRepository.class);
    private final ProblemStatementRepository problemStatementRepository = mock(ProblemStatementRepository.class);
    private final UserSelectionRepository userSelectionRepository = mock(UserSelectionRepository.class);
    private final AdminService adminService = new AdminService(
        adminRepository,
        problemStatementRepository,
        userSelectionRepository
    );

    @Test
    void uploadProcessesFirstDataRowWhenProblemIdIsText() throws Exception {
        MockMultipartFile file = workbookFile(workbook -> {
            Sheet sheet = workbook.createSheet("Problems");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("PS001");
            row.createCell(1).setCellValue("Smart Attendance");
            row.createCell(2).setCellValue("Build an attendance system.");
        });

        when(problemStatementRepository.findByProblemId("PS001")).thenReturn(Optional.empty());

        String result = adminService.uploadProblemStatementsFromExcel(file);

        ArgumentCaptor<ProblemStatement> captor = ArgumentCaptor.forClass(ProblemStatement.class);
        verify(problemStatementRepository).save(captor.capture());
        ProblemStatement savedProblem = captor.getValue();

        assertThat(result).isEqualTo("Successfully uploaded 1 problem statements out of 1 rows");
        assertThat(savedProblem.getProblemId()).isEqualTo("PS001");
        assertThat(savedProblem.getMaxUsers()).isEqualTo(2);
        assertThat(savedProblem.getCurrentUsers()).isZero();
        assertThat(savedProblem.getIsAvailable()).isTrue();
    }

    @Test
    void uploadKeepsProblemStatementsLimitedToTwoUsers() throws Exception {
        ProblemStatement existingProblem = new ProblemStatement();
        existingProblem.setId(10L);
        existingProblem.setProblemId("PS002");
        existingProblem.setCurrentUsers(1);
        existingProblem.setMaxUsers(5);
        existingProblem.setIsAvailable(true);

        MockMultipartFile file = workbookFile(workbook -> {
            Sheet sheet = workbook.createSheet("Problems");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Problem ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Description");
            header.createCell(3).setCellValue("Max Users");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("PS002");
            row.createCell(1).setCellValue("Updated Title");
            row.createCell(2).setCellValue("Updated description.");
            row.createCell(3).setCellValue(5);
        });

        when(problemStatementRepository.findByProblemId("PS002")).thenReturn(Optional.of(existingProblem));

        adminService.uploadProblemStatementsFromExcel(file);

        verify(problemStatementRepository).findByProblemId(eq("PS002"));
        verify(problemStatementRepository).save(any(ProblemStatement.class));
        assertThat(existingProblem.getMaxUsers()).isEqualTo(2);
        assertThat(existingProblem.getIsAvailable()).isTrue();
    }

    @Test
    void uploadAcceptsBundledShortlistedWorkbook() throws Exception {
        ClassPathResource resource = new ClassPathResource("Consolidated-Problem-Statements_V2 (2).xlsx");
        MockMultipartFile file = new MockMultipartFile(
            "file",
            resource.getFilename(),
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            resource.getInputStream()
        );

        when(problemStatementRepository.findByProblemId(anyString())).thenReturn(Optional.empty());

        String result = adminService.uploadProblemStatementsFromExcel(file);

        ArgumentCaptor<ProblemStatement> captor = ArgumentCaptor.forClass(ProblemStatement.class);
        verify(problemStatementRepository, times(53)).save(captor.capture());

        ProblemStatement firstProblem = captor.getAllValues().get(0);
        assertThat(result).isEqualTo("Successfully uploaded 53 problem statements out of 53 rows");
        assertThat(firstProblem.getProblemId()).isEqualTo("1");
        assertThat(firstProblem.getTitle()).contains("Regulatory & Policy Change Impact Analyzer");
        assertThat(firstProblem.getMaxUsers()).isEqualTo(2);
    }

    private MockMultipartFile workbookFile(WorkbookWriter writer) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writer.write(workbook);
            workbook.write(outputStream);
            return new MockMultipartFile(
                "file",
                "problem-statements.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray()
            );
        }
    }

    @FunctionalInterface
    private interface WorkbookWriter {
        void write(Workbook workbook);
    }
}
