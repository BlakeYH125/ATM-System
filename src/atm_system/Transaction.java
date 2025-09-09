package atm_system;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    private Date date;
    private String type;
    private double amount;

    public Transaction(Date date, String type, double amount) {
        this.date = date;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return "Транзакция. " + sdf.format(date) +
                    " - " + type +
                    ", сумма: " + amount;
    }
}
