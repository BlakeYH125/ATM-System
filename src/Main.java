import atm_system.ATM;
import atm_system.Bank;

public class Main {
    public static void main(String[] args) {
        Bank tinkoff = new Bank();
        ATM.start(tinkoff);
    }
}