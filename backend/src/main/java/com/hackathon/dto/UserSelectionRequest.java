package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSelectionRequest {
    private String teamName;        // Team name
    private String teamLeadName;    // Team lead's name
    private String teamLeadEmail;   // Team lead's email (Must be unique and end with @htcinc.com)
    private Long problemStatementId; // Selected problem ID
}
