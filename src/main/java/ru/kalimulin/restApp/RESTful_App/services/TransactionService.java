package ru.kalimulin.restApp.RESTful_App.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kalimulin.restApp.RESTful_App.exceptions.AccountNotFoundException;
import ru.kalimulin.restApp.RESTful_App.exceptions.InsufficientBalanceException;
import ru.kalimulin.restApp.RESTful_App.exceptions.InvalidPinException;
import ru.kalimulin.restApp.RESTful_App.models.Account;
import ru.kalimulin.restApp.RESTful_App.models.Transaction;
import ru.kalimulin.restApp.RESTful_App.repositories.AccountRepository;
import ru.kalimulin.restApp.RESTful_App.repositories.TransactionRepository;
import ru.kalimulin.restApp.RESTful_App.util.TransactionType;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    //Пополнение счета
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, amount, TransactionType.DEPOSIT);
        transactionRepository.save(transaction);
    }

    //Снятие средств + проверка пин-кода
    public void withdraw(String accountNumber, String pin, BigDecimal amount) {
        Account account = getAccount(accountNumber);

        validPin(account, pin);
        validBalance(account, amount);

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, amount.negate(), TransactionType.WITHDRAWAL);

        transactionRepository.save(transaction);
    }

    //Перевод средств между счетами
    public void transfer(String from, String pin, String to, BigDecimal amount) {

        Account fromAccount = getAccount(from);
        Account toAccount = getAccount(to);

        validPin(fromAccount, pin);

        validBalance(fromAccount, amount);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction fromTransaction = new Transaction(fromAccount, amount.negate(), TransactionType.TRANSFER);
        Transaction toTransaction = new Transaction(toAccount, amount, TransactionType.TRANSFER);

        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);
    }

    //Получение истории транзакций для определенного счета
    public List<Transaction> getTransactionByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    /**
     * Проверяет, соответствует ли указанный PIN-код хэшированному PIN-коду учетной записи.
     *
     * @param account учетная запись, для которой необходимо проверить PIN-код
     * @param pin     PIN-код, который нужно проверить на соответствие
     * @return {@code true}, если указанный PIN-код соответствует хэшированному PIN-коду;
     * {@code false} в противном случае
     */
    private boolean checkPin(Account account, String pin) {
        return BCrypt.checkpw(pin, account.getPinHash());
    }


    /**
     * Получает учетную запись по указанному номеру счета.
     *
     * @param accountNumber номер счета, для которого необходимо получить учетную запись
     * @return объект {@link Account}, соответствующий указанному номеру счета
     * @throws RuntimeException если учетная запись с указанным номером счета не найдена
     */
    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    /**
     * Проверяет, соответствует ли указанный PIN-код хэшированному PIN-коду на счету.
     *
     * @param account учетная запись, для которой необходимо проверить PIN-код
     * @param pin     PIN-код, который нужно проверить на соответствие
     * @throws RuntimeException если указанный PIN-код не совпадает с хешированным PIN-кодом учетной записи
     */
    private void validPin(Account account, String pin) {
        if (!checkPin(account, pin)) {
            throw new InvalidPinException("Pin doesn't match");
        }
    }

    /**
     * Проверяет, достаточно ли средств на счете для выполнения операции.
     *
     * @param account учетная запись, для которой необходимо проверить баланс
     * @param amount  сумма, которую необходимо проверить
     * @throws RuntimeException если баланс счета меньше требуемой суммы
     */
    private void validBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }
}
