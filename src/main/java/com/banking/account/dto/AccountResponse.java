package com.banking.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing account details")
public class AccountResponse {

    @Schema(
            description = "Unique identifier for the account",
            example = "ACC17304567891234ABCD"
    )
    private String accountId;

    @Schema(
            description = "Unique identifier for the customer",
            example = "CUST123456"
    )
    private String customerId;

    @Schema(
            description = "Type of account",
            example = "SAVINGS",
            allowableValues = {"SAVINGS", "CURRENT", "SALARY", "FIXED_DEPOSIT"}
    )
    private String accountType;

    @Schema(
            description = "ISO 4217 three-letter currency code",
            example = "USD"
    )
    private String currency;

    @Schema(
            description = "Current status of the account",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED", "CLOSED"}
    )
    private String status;

    @Schema(
            description = "Full name of the customer",
            example = "John Doe"
    )
    private String customerName;

    @Schema(
            description = "Email address of the customer",
            example = "john.doe@example.com"
    )
    private String email;

    @Schema(
            description = "Phone number of the customer",
            example = "+11234567890"
    )
    private String phoneNumber;

    @Schema(
            description = "Timestamp when the account was created",
            example = "2024-11-08T10:30:00"
    )
    private String createdAt;

    @Schema(
            description = "Timestamp when the account was last updated",
            example = "2024-11-08T15:45:30"
    )
    private String updatedAt;
}
