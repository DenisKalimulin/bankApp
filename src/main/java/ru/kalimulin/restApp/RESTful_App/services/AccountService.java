package ru.kalimulin.restApp.RESTful_App.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kalimulin.restApp.RESTful_App.models.Account;
import ru.kalimulin.restApp.RESTful_App.repositories.AccountRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
    Метод для создания аккаунта.
    Генерирует номер счета с помощью приватного метода generateAccountNumber()
    Хэширует пин-код с помощью приватного метода hashPin()
    Выставляет баланс 0 при создании аккаунта
     */
    public Account createAccount(String name, String pin) {
        Account account = new Account();
        account.setName(name);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setPinHash(hashPin(pin));
        account.setBalance(BigDecimal.ZERO);

        return accountRepository.save(account);
    }

    //Получение одного аккаунта по ID
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    //Получение всех аккаунтов
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    /**
     * Генерирует уникальный номер банковского счета.
     *
     * @return Уникальный номер счета.
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    /**
     * Генерирует случайный номер банковского счета, состоящий из 12 цифр.
     *
     * @return Сгенерированный номер счета в виде строки.
     */
    private String generateAccountNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 12; i++) {
            stringBuilder.append(secureRandom.nextInt(10)); // Добавляем случайную цифру от 0 до 9
        }
        return stringBuilder.toString(); // Возвращаем сгенерированный номер счета
    }


    /**
     * Хеширует указанный PIN-код с использованием BCrypt.
     *
     * @param pin PIN-код, который необходимо хешировать
     * @return Хешированный PIN-код
     */
    private String hashPin(String pin) {
        return BCrypt.hashpw(pin, BCrypt.gensalt());
    }
}
