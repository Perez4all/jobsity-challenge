package com.jobsity.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
    private String errorMessage;
    private int status;
}
