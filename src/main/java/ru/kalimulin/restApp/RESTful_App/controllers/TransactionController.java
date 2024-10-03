package ru.kalimulin.restApp.RESTful_App.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.restApp.RESTful_App.models.Transaction;
import ru.kalimulin.restApp.RESTful_App.services.TransactionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable String accountNumber, @RequestParam BigDecimal amount){
        transactionService.deposit(accountNumber, amount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{accountNumber}/withdraw")
    public void withdraw(@PathVariable String accountNumber,
                         @RequestParam BigDecimal amount,
                         @RequestParam String pin){
        transactionService.withdraw(accountNumber, pin, amount);
    }

    @PostMapping("/{fromAccountNumber}/transfer")
    public void transfer(@PathVariable String fromAccountNumber,
                         @RequestParam String toAccountNumber,
                         @RequestParam BigDecimal amount,
                         @RequestParam String pin){
        transactionService.transfer(fromAccountNumber, pin, toAccountNumber, amount);
    }

    @GetMapping("/{accountId}")
    public List<Transaction> getAllTransactions(@PathVariable Long accountId){
        return transactionService.getTransactionByAccountId(accountId);
    }
}
