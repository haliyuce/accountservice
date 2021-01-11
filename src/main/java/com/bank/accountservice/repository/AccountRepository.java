package com.bank.accountservice.repository;

import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<List<Account>> findAllByTypeIn(Set<AccountType> accountTypes);
}
