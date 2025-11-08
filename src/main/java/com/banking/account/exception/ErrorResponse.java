
package com.banking.account.exception;
import java.time.LocalDateTime;
import java.util.Map;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@io.swagger.v3.oas.annotations.media.Schema(description = "Error response object containing details about the error")
public class ErrorResponse {

    @io.swagger.v3.oas.annotations.media.Schema(
            description = "Timestamp when the error occurred",
            example = "2024-11-08T10:30:00"
    )
    private LocalDateTime timestamp;

    @io.swagger.v3.oas.annotations.media.Schema(
            description = "HTTP status code",
            example = "400"
    )
    private int status;

    @io.swagger.v3.oas.annotations.media.Schema(
            description = "Error type/category",
            example = "Bad Request"
    )
    private String error;

    @io.swagger.v3.oas.annotations.media.Schema(
            description = "Detailed error message",
            example = "Invalid request parameters"
    )
    private String message;

    @io.swagger.v3.oas.annotations.media.Schema(
            description = "Map of field-specific validation errors (only present for validation failures)",
            example = "{\"email\": \"Invalid email format\", \"customerId\": \"Customer ID is required\"}"
    )
    private Map<String, String> validationErrors;
}