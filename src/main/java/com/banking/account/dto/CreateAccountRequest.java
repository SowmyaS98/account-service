package com.banking.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new account")
public class CreateAccountRequest {

    @Schema(
            description = "Unique identifier for the customer",
            example = "CUST123456",
            required = true
    )
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Schema(
            description = "Type of account to be created",
            example = "SAVINGS",
            required = true,
            allowableValues = {"SAVINGS", "CURRENT", "SALARY", "FIXED_DEPOSIT"}
    )
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT|SALARY|FIXED_DEPOSIT", message = "Invalid account type")
    private String accountType;

    @Schema(
            description = "ISO 4217 three-letter currency code",
            example = "USD",
            required = true,
            pattern = "[A-Z]{3}",
            minLength = 3,
            maxLength = 3
    )
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be 3-letter ISO code")
    private String currency;

    @Schema(
            description = "Full name of the customer",
            example = "John Doe",
            required = true,
            maxLength = 255
    )
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Schema(
            description = "Email address of the customer (must be unique)",
            example = "john.doe@example.com",
            required = true,
            format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(
            description = "Phone number in E.164 format (optional)",
            example = "+11234567890",
            pattern = "^\\+?[1-9]\\d{1,14}$"
    )
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
}