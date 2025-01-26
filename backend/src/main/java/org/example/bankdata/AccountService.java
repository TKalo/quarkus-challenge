package org.example.bankdata;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(AccountInput input) {
        Account account = new Account();
        account.setFirstName(input.getFirstName());
        account.setLastName(input.getLastName());
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(0.0);
        accountRepository.persist(account);
        return account;
    }
}
