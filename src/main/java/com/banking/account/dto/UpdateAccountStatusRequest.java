package com.banking.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating account status")
public class UpdateAccountStatusRequest {

    @Schema(
            description = "New status for the account. Valid transitions: ACTIVE<->INACTIVE, ACTIVE->SUSPENDED, ACTIVE/INACTIVE/SUSPENDED->CLOSED",
            example = "SUSPENDED",
            required = true,
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED", "CLOSED"}
    )
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED|CLOSED", message = "Invalid status")
    private String status;

    @Schema(
            description = "Reason for the status change (optional but recommended for audit purposes)",
            example = "Suspicious activity detected"
    )
    private String reason;
}
