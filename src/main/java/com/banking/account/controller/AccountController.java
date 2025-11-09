package com.banking.account.controller;

import com.banking.account.dto.CreateAccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.UpdateAccountStatusRequest;
import com.banking.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.io.opentelemetry.proto.metrics.v1.Summary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Management", description = "APIs for managing customer accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(
            summary = "Create a new account",
            description = "Creates a new customer account and publishes an account creation event to Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account with email already exists"
            )
    })
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        log.info("Received create account request for customer: {}", request.getCustomerId());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Get account by ID",
            description = "Retrieves account details by account ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Account ID", example = "ACC123456")
            @PathVariable String accountId) {
        log.info("Received get account request: accountId={}", accountId);
        AccountResponse response = accountService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
            summary = "Get accounts by customer ID",
            description = "Retrieves all accounts for a specific customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            )
    })
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(
            @Parameter(description = "Customer ID", example = "CUST123")
            @PathVariable String customerId) {
        log.info("Received get accounts request for customer: {}", customerId);
        List<AccountResponse> response = accountService.getAccountsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email/{email}")
    @Operation(
            summary = "Get Account details by user email id",
            description = "Retrieves account for a specific customer by email"

    )
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully"
            )
    })
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerEmail(
            @Parameter(description = "Customer Email", example="john.doe@gmail.com")
            @PathVariable String email) {
        log.info("Received get accounts request for customer email: {}", email);
        List<AccountResponse> response = accountService.getAccountByCustomerEmail(email);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{accountId}/status")
    @Operation(
            summary = "Update account status",
            description = "Updates the status of an account (ACTIVE, INACTIVE, SUSPENDED, CLOSED)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account status updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @Parameter(description = "Account ID", example = "ACC123456")
            @PathVariable String accountId,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        log.info("Received update account status request: accountId={}, newStatus={}",
                accountId, request.getStatus());
        AccountResponse response = accountService.updateAccountStatus(accountId, request);
        return ResponseEntity.ok(response);
    }
}