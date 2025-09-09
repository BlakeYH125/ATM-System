package atm_system;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Bank {
    private Map<String, User> users;

    void registerUser(User user) {
        users.put(user.getLogin(), user);
        safeFile();

    }

    void removeUser(String login) {
        users.remove(login);
        File file = new File("users.txt");
        writeUsersInFile(file);
    }

    public Bank() {
        this.users = new HashMap<>();
        File file = new File("users.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readUsersFromFile(file);
    }

    User findUser(String login) {
        User user = users.get(login);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден.");
        } else {
            return user;
        }
    }

    Map<String, User> getUsers() {
        return users;
    }

    void transfer(String fromLogin, String toLogin, double amount) {
        User userSender = findUser(fromLogin);
        User userGetter = findUser(toLogin);
        try {
            userSender.transfer(amount);
            userGetter.deposit(amount);
            System.out.println("*Перевод успешен!*");
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeUsersInFile(File file) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(users);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readUsersFromFile(File file) {
        if (file.length() != 0) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                users = (HashMap<String, User>) objectInputStream.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    void safeFile() {
        File file = new File("users.txt");
        writeUsersInFile(file);
    }
}
