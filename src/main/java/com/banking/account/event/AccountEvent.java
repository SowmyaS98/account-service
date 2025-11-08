package com.banking.account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {

    private String eventId;
    private String accountId;
    private String customerId;
    private String accountType;
    private String currency;
    private String status;
    private String customerName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private AccountEventType eventType;
    private LocalDateTime eventTimestamp;

    public enum AccountEventType {
        ACCOUNT_CREATED,
        ACCOUNT_UPDATED,
        ACCOUNT_SUSPENDED,
        ACCOUNT_CLOSED,
        ACCOUNT_REACTIVATED
    }
}
