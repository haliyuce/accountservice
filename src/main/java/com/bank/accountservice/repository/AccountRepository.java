package com.bank.accountservice.repository;

import com.bank.accountservice.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
