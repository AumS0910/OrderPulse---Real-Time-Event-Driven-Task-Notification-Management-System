package org.orderpulse.orderpulsebackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO class for standardized error responses across the API.
 * Provides a consistent structure for error information returned to clients.
 */
@Data
@Builder
public class ErrorResponse {
    private String message;        // Main error message
    private String details;        // Additional error details if available
    private String path;          // The API endpoint that generated the error
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;  // When the error occurred
    
    private String errorCode;      // Application-specific error code
    private int status;           // HTTP status code
}