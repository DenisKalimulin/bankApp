package ru.kalimulin.restApp.RESTful_App.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.restApp.RESTful_App.models.Account;
import ru.kalimulin.restApp.RESTful_App.services.AccountService;


@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@RequestParam String name, @RequestParam String pin) {
        return accountService.createAccount(name, pin);
    }

    @GetMapping
    public Iterable<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable long accountId) {
        return accountService.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
