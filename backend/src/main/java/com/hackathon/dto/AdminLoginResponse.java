package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private Long adminId;
    private String username;
    private String fullName;
    private String email;
    private String message;
    private Boolean success;
}
