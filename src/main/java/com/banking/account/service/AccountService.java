package com.banking.account.service;

import com.banking.account.domain.Account;
import com.banking.account.domain.AccountStatus;
import com.banking.account.domain.AccountType;
import com.banking.account.dto.CreateAccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.UpdateAccountStatusRequest;
import com.banking.account.event.AccountEvent;
import com.banking.account.exception.AccountAlreadyExistsException;
import com.banking.account.exception.AccountNotFoundException;
import com.banking.account.kafka.AccountEventProducer;
import com.banking.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEventProducer eventProducer;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerId());

        // Check if email already exists
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AccountAlreadyExistsException("Account with email " + request.getEmail() + " already exists");
        }

        // Generate account ID
        String accountId = generateAccountId();

        // Create account entity
        Account account = Account.builder()
                .accountId(accountId)
                .customerId(request.getCustomerId())
                .accountType(AccountType.valueOf(request.getAccountType()))
                .currency(request.getCurrency())
                .status(AccountStatus.ACTIVE)
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        // Save to database
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: accountId={}", accountId);

        // Publish account created event
        AccountEvent event = buildAccountEvent(savedAccount, AccountEvent.AccountEventType.ACCOUNT_CREATED);
        eventProducer.publishAccountEvent(event);

        return mapToResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountId) {
        log.info("Fetching account: accountId={}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
        return mapToResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomer(String customerId) {
        log.info("Fetching accounts for customer: {}", customerId);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse updateAccountStatus(String accountId, UpdateAccountStatusRequest request) {
        log.info("Updating account status: accountId={}, newStatus={}", accountId, request.getStatus());

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        AccountStatus oldStatus = account.getStatus();
        AccountStatus newStatus = AccountStatus.valueOf(request.getStatus());

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        account.setStatus(newStatus);
        Account updatedAccount = accountRepository.save(account);

        // Publish appropriate event based on status change
        AccountEvent.AccountEventType eventType = mapStatusToEventType(newStatus);
        AccountEvent event = buildAccountEvent(updatedAccount, eventType);
        eventProducer.publishAccountEvent(event);

        log.info("Account status updated: accountId={}, oldStatus={}, newStatus={}",
                accountId, oldStatus, newStatus);

        return mapToResponse(updatedAccount);
    }

    private String generateAccountId() {
        // Generate unique account ID with prefix
        return "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AccountEvent buildAccountEvent(Account account, AccountEvent.AccountEventType eventType) {
        return AccountEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .accountId(account.getAccountId())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType().name())
                .currency(account.getCurrency())
                .status(account.getStatus().name())
                .customerName(account.getCustomerName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .createdAt(account.getCreatedAt())
                .eventType(eventType)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType().name())
                .currency(account.getCurrency())
                .status(account.getStatus().name())
                .customerName(account.getCustomerName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .createdAt(account.getCreatedAt().toString())
                .updatedAt(account.getUpdatedAt() != null ? account.getUpdatedAt().toString() : null)
                .build();
    }

    private void validateStatusTransition(AccountStatus oldStatus, AccountStatus newStatus) {
        // Business rules for status transitions
        if (oldStatus == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed account");
        }

        if (oldStatus == newStatus) {
            throw new IllegalStateException("Account is already in " + newStatus + " status");
        }
    }

    private AccountEvent.AccountEventType mapStatusToEventType(AccountStatus status) {
        return switch (status) {
            case ACTIVE -> AccountEvent.AccountEventType.ACCOUNT_REACTIVATED;
            case SUSPENDED -> AccountEvent.AccountEventType.ACCOUNT_SUSPENDED;
            case CLOSED -> AccountEvent.AccountEventType.ACCOUNT_CLOSED;
            default -> AccountEvent.AccountEventType.ACCOUNT_UPDATED;
        };
    }
}