package ru.kalimulin.restApp.RESTful_App.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kalimulin.restApp.RESTful_App.models.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
}
