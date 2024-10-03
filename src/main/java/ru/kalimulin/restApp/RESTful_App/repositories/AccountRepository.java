package ru.kalimulin.restApp.RESTful_App.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kalimulin.restApp.RESTful_App.models.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
