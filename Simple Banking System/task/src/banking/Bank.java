package banking;

import net.andreinc.mockneat.MockNeat;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static net.andreinc.mockneat.types.enums.CreditCardType.*;

public class Bank {
    public static String IIN = "400000";
    private final Map<String, Account> accounts = new HashMap<>();
    static Account currentAccount;

    public void initMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    public void loginMenu() {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }

    

    public void createAccount() {
        Account account = Account.createNewAccount();
        Database.saveAccount(account);
        System.out.println("Your card has been created");
        System.out.printf("Your card number:\n%s\n", Account.getCardNumber());
        System.out.printf("Your card PIN:\n%s\n", account.getPin());
    }

    public boolean loginAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        int pin = Integer.parseInt(scanner.nextLine());

        boolean isValidCheckSum = confirmCheckSum(cardNumber);
        Account account = Database.loadAccount(cardNumber, pin);

        if (!isValidCheckSum || account == null || account.getPin() != pin){
            System.out.println("Wrong card number or PIN!");
            return false;
        }

        System.out.println("You have successfully logged in!");
        currentAccount = account;
        return true;
    }

    public static boolean isCardValid(String cardNumber){
        return confirmCheckSum(cardNumber);
    }

    private static boolean confirmCheckSum(String cardNumber) {
        return Account.getLuhnAlgorithmSum(cardNumber) % 10 == 0;
    }


    public void printBalance() {
        currentAccount.printBalance();
        //System.out.println(Account.getBalance());
    }

    public static int updatedBalance(int balance, int amount){
        return balance-amount;
    }

    public boolean logoutAccount() {
        currentAccount = null;
        System.out.println("You have successfully logged out!");
        return false;
    }

    public void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }
}
