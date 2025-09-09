package atm_system;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class User implements Serializable {
    private String login;
    private String passwordHash;
    private String salt;
    private String fullName;
    private double balance;
    private List<Transaction> transactions;
    private Date banDate;

    public User(String login, String password, String fullName, double balance) {
        Scanner scanner = new Scanner(System.in);
        this.login = login;
        this.fullName = fullName;
        this.transactions = new ArrayList<Transaction>();
        this.banDate = new Date();
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, salt);
        this.balance = balance;
    }

    private String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    void changePassword(String password) {
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, this.salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    Date getBanDate() {
        return banDate;
    }

    boolean isBanned() {
        return new Date().before(banDate);
    }

    void banUser() {
        banDate = new Date(System.currentTimeMillis() + 300_000);
    }

    String getLogin() {
        return login;
    }

    double getBalance() {
        return balance;
    }

    boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password, this.salt));
    }

    void deposit(double amount) {
        balance += amount;
        addTransaction(new Transaction(new Date(), "ПОПОЛНЕНИЕ", amount));
    }

    void withdraw(double amount) {
        if (balance < amount) {
            throw new InsufficientFundsException("*Недостаточно средств!*");
        } else {
            balance -= amount;
            addTransaction(new Transaction(new Date(), "СНЯТИЕ НАЛИЧНЫХ", amount));
        }
    }

    void transfer(double amount) {
        if (balance < amount) {
            throw new InsufficientFundsException("*Недостаточно средств!*");
        } else {
            balance -= amount;
            addTransaction(new Transaction(new Date(), "ПЕРЕВОД", amount));
        }
    }

    void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Double.compare(balance, user.balance) == 0 && Objects.equals(login, user.login) && Objects.equals(passwordHash, user.passwordHash) && Objects.equals(salt, user.salt) && Objects.equals(fullName, user.fullName) && Objects.equals(transactions, user.transactions) && Objects.equals(banDate, user.banDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, passwordHash, salt, fullName, balance, transactions, banDate);
    }
}
