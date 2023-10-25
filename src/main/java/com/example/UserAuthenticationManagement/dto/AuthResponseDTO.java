package com.example.UserAuthenticationManagement.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuthResponseDTO {

	private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Boolean isMfaRequired;
    private Date expiresIn;
}
