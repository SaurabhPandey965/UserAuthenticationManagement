package com.example.UserAuthenticationManagement.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    private Integer httpStatus;
    private String exception;
    private String code;
    private String message;
    private List<FieldError> fieldErrors;

}
