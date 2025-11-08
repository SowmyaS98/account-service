package com.banking.account.repository;

import com.banking.account.domain.Account;
import com.banking.account.domain.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByCustomerId(String customerId);

    List<Account> findByStatus(AccountStatus status);

    Optional<Account> findByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.customerId = :customerId AND a.status = :status")
    List<Account> findActiveAccountsByCustomer(
            @Param("customerId") String customerId,
            @Param("status") AccountStatus status
    );

    boolean existsByAccountId(String accountId);

    boolean existsByEmail(String email);
}