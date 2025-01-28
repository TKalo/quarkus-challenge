package org.example.bankdata.account;

import java.util.List;
import java.util.UUID;

import org.example.bankdata.account.input.AccountInput;
import org.example.bankdata.currency.CurrencyService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository, CurrencyService exchangeRateService) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountModel createAccount(AccountInput input) {
        AccountModel account = new AccountModel();
        account.setFirstName(input.getFirstName());
        account.setLastName(input.getLastName());
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(0.0);
        accountRepository.persist(account);
        return account;
    }

    @Transactional
    public AccountModel depositMoney(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        AccountModel account = accountRepository.find("accountNumber", accountNumber).firstResult();
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.persist(account);
        return account;
    }

    @Transactional
    public void transferMoney(String fromAccount, String toAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        AccountModel source = accountRepository.find("accountNumber", fromAccount).firstResult();
        AccountModel destination = accountRepository.find("accountNumber", toAccount).firstResult();

        if (source == null) {
            throw new NotFoundException("Source account not found");
        }

        if (destination == null) {
            throw new NotFoundException("Destination account not found");
        }

        if (source.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        source.setBalance(source.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);

        accountRepository.persist(source);
        accountRepository.persist(destination);
    }

    @Transactional
    public AccountModel getAccount(String accountNumber) {
        AccountModel account = accountRepository.find("accountNumber", accountNumber).firstResult();
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        return account;
    }

    @Transactional
    public List<AccountModel> getAllAccounts() {
        return accountRepository.listAll();
    }
}
