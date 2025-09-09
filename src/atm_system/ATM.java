package atm_system;

import java.util.Scanner;

public class ATM {
    private static final String EXITCODE = "0";
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void start(Bank bank) {
        String input;
        while (true) {
            System.out.println("Для входа в аккаунт введите \"1\"");
            System.out.println("Для регистрации введите \"2\"");
            System.out.println("Для выхода из программы введите \"0\"");

            input = SCANNER.nextLine();
            switch (input) {
                case "2":
                    register(bank);
                    continue;
                case EXITCODE:
                    save(bank);
                    SCANNER.close();
                    System.exit(0);
                case "1":
                    login(bank);
                    break;
                default:
                    System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }

    private static void register(Bank bank) {
        String newLogin = "";
        boolean isLoginFound = false;
        System.out.println("Придумайте и введите логин: ");
        while (!isLoginFound) {
            newLogin = SCANNER.nextLine();
            if (hasUser(bank, newLogin)) {
                System.out.println("Данный логин уже занят. Введите другой: ");
            } else if (newLogin.isEmpty()) {
                System.out.println("Логин не может быть нулевой длины. Введите другой: ");
            } else if (newLogin.contains(" ")){
                newLogin.contains("Логин не может содержать пробелы. Введите другой: ");
            } else {
                isLoginFound = true;
            }
        }
        System.out.println("Придумайте и введите пароль: ");
        String newPassword = SCANNER.nextLine();
        while (true) {
            if (newPassword.isEmpty()) {
                System.out.println("Пароль не может быть нулевой длины. Введите другой: ");
                newPassword = SCANNER.nextLine();
            } else if (newPassword.contains(" ")) {
                System.out.println("Логин не может содержать пробелы. Введите другой: ");
            } else {
                break;
            }
        }
        System.out.println("Введите ваши имя и фамилию через пробел: ");
        String fullName = SCANNER.nextLine();
        System.out.println("Какую сумму вы сразу внесете на счет?");
        double balance = inputAndCheckNumber();
        bank.registerUser(new User(newLogin, newPassword, fullName, balance));
        System.out.println("Аккаунт успешно создан!");
    }

    private static void login(Bank bank) {
        String login;
        boolean isUserFound = false;
        boolean isPasswordCorrect = false;

        while (!isUserFound) {
            System.out.println("Введите ваш логин или \"0\" для возврата в главное меню: ");
            login = SCANNER.nextLine();
            if (login.equals("0")) {
                break;
            }
            try {
                User user = bank.findUser(login);
                if (user.isBanned()) {
                    System.out.println("Превышено количество попыток ввода пароля.");
                    System.out.println("Войти в аккаунт снова вы сможете в " + user.getBanDate());
                    continue;
                }
                isUserFound = true;
                String password;
                for (int i = 0; i < 3; i++) {
                    System.out.println("Введите пароль: ");
                    password = SCANNER.nextLine();
                    if (user.checkPassword(password)) {
                        isPasswordCorrect = true;
                        break;
                    } else {
                        if (i == 0) {
                            System.out.println("Неверный пароль, попробуйте снова. Осталось " + 2 + " попытки.");
                        } else if (i == 1) {
                            System.out.println("Неверный пароль, попробуйте снова. Осталось " + 1 + " попытка.");
                        } else {
                            System.out.println("Неверный пароль, попробуйте снова позднее.");
                            user.banUser();
                        }
                    }
                }
                if (!isPasswordCorrect) {
                    isUserFound = false;
                    continue;
                } else {
                    userMenu(bank, user, login);
                    continue;
                }
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void userMenu(Bank bank, User user, String login) {
        String confirmPassword;
        boolean isGoExit = false;
        while (!isGoExit) {
            double amount;
            showMenu();
            String doing = SCANNER.nextLine();
            switch (doing) {
                case EXITCODE:
                    isGoExit = true;
                    break;
                case "1":
                    System.out.println("Текущий баланс счета: " + user.getBalance());
                    break;
                case "2":
                    amount = enterAmountOrReturn();
                    if (amount == 0) {
                        break;
                    }
                    try {
                        user.deposit(amount);
                        System.out.println("*Пополнение успешно!*");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверно набрана сумма!");
                    }
                    break;
                case "3":
                    amount = enterAmountOrReturn();
                    if (amount == 0) {
                        break;
                    }
                    try {
                        user.withdraw(amount);
                        System.out.println("*Снятие успешно!*");
                    } catch (InsufficientFundsException e) {
                        System.out.println(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверно набрана сумма!");
                    }
                    break;
                case "4":
                    boolean isGetterFound = false;
                    while (!isGetterFound) {
                        System.out.println("Введите логин получателя или \"0\" для возврата в меню: ");
                        String getter = SCANNER.nextLine();
                        if (getter.equals("0")) {
                            break;
                        }
                        if (!getter.equals(login)) {
                            try {
                                bank.findUser(getter);
                                isGetterFound = true;
                                amount = enterAmountOrReturn();
                                if (amount == 0) {
                                    break;
                                }
                                bank.transfer(login, getter, amount);
                            } catch (UserNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Вы ввели свой же логин. Укажите другой.");
                        }
                    }
                    break;
                case "5":
                    if (user.getTransactions().isEmpty()) {
                        System.out.println("Список транзакций пуст.");
                        break;
                    }
                    for (Transaction transaction : user.getTransactions()) {
                        System.out.println(transaction);
                    }
                    break;
                case "6":
                    confirmPassword = requestPassword();
                    if (checkPassword(bank, login, confirmPassword)) {
                        System.out.println("Введите новый пароль: ");
                        String newPasswordToChange = SCANNER.nextLine();
                        bank.findUser(login).changePassword(newPasswordToChange);
                        System.out.println("Смена пароля успешна! Войдите в аккаунт заново");

                    } else {
                        System.out.println("Пароль неверный. Войдите в аккаунт заново.");
                    }
                    isGoExit = true;
                    break;
                case "7":
                    confirmPassword = requestPassword();
                    if (checkPassword(bank, login, confirmPassword)) {
                        bank.removeUser(login);
                        System.out.println("Удаление профиля успешно!");
                    } else {
                        System.out.println("Пароль неверный. Войдите в аккаунт заново.");
                    }
                    isGoExit = true;
                    break;
                default:
                    System.out.println("Неверный номер действия. Попробуйте снова.");
            }
            save(bank);
        }
    }
    private static double inputAndCheckNumber() {
        while (true) {
            try {
                double number = Double.parseDouble(SCANNER.nextLine());
                if (number >= 0) {
                    return number;
                } else {
                    System.out.println("Введено некорректное значение! Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    private static void save(Bank bank) {
        bank.safeFile();
    }

    private static boolean checkPassword(Bank bank, String login, String confirmPassword) {
        return bank.findUser(login).checkPassword(confirmPassword);
    }

    private static double enterAmountOrReturn() {
        System.out.println("Введите сумму или \"0\" для возврата в меню: ");
        return inputAndCheckNumber();
    }

    private static String requestPassword() {
        System.out.println("Подтвердите действие. Введите текущий пароль");
        return SCANNER.nextLine();
    }

    private static void showMenu() {
        System.out.println("Введите действие, которое хотите выполнить: ");
        System.out.println("1. Проверить баланс");
        System.out.println("2. Пополнить счет");
        System.out.println("3. Снять деньги");
        System.out.println("4. Перевести деньги другому человеку");
        System.out.println("5. Запросить историю по счету");
        System.out.println("6. Сменить пароль");
        System.out.println("7. Удалить свой профиль");
        System.out.println("0. Выход");
    }

    private static boolean hasUser(Bank bank, String newLogin) {
        return bank.getUsers().containsKey(newLogin);
    }
}