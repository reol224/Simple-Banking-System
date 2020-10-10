package banking;

import java.util.Scanner;

public class Main {
    static Bank bank = new Bank();
    static String dbFilename = "Bank.db";
    private static boolean isLoggedIn;

    public static void main(String[] args) {
        argsParse(args);
        if (dbFilename.equals("")){
            System.out.println("Database not available, Exiting...");
            System.exit(0);
        }
        Database.initDB(dbFilename);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (!isLoggedIn) {
                bank.initMenu();
                int action = Integer.parseInt(scanner.nextLine());
                chooseActionInitMenu(action);
            } else {
                bank.loginMenu();
                int action = Integer.parseInt(scanner.nextLine());
                chooseActionLoginMenu(action);
            }
            //int action = Integer.parseInt(scanner.nextLine());
            //chooseActionInitMenu(action);
        }
    }
    private static void chooseActionLoginMenu(int action){
        switch (action) {
            case 1:
                if(isLoggedIn){
                    bank.printBalance();
                }
                break;
            case 2:
                System.out.println("Enter income:");
                Scanner scanner = new Scanner(System.in);
                int income = scanner.nextInt();
                Database.addIncome(bank.currentAccount,income);
                System.out.println("Income was added!");
                break;
            case 3: {
                Scanner scannerX = new Scanner(System.in);
                System.out.println("Transfer");
                System.out.println("Enter card number");
                String cardNumber = scannerX.nextLine();
                if (cardNumber.equals(Account.getCardNumber())) {
                    System.out.println("You can't transfer money to the same account!");
                    break;
                }
                //check if number given matches luhn alg
                else if (!Bank.isCardValid(cardNumber)) {
                    System.out.println("Probably you made mistake in the card number.Please try again!");
                    break;
                }
                //check if card exists in db
                else if (!Database.cardExistsInDB(cardNumber)) {
                    System.out.println("The card that you're trying to transfer money to doesn't exist!");
                    break;
                }
                System.out.println("Enter how much money you want to transfer");
                int amount = scannerX.nextInt();
                //not enough money
                if (amount > Account.getBalance()) {
                    System.out.println("Not enough money!");
                    break;
                }
                else {
                    //Database.withdrawMoney(bank.currentAccount,amount);
                    Database.transferMoney(bank.currentAccount, amount, cardNumber);
                    Bank.updatedBalance(Account.getBalance(), amount);
                    System.out.println("Success!");
                }
            }
            break;
            case 4:
                Database.closeAccount(bank.currentAccount);
                isLoggedIn = bank.logoutAccount();
                break;
            case 5:
                isLoggedIn = bank.logoutAccount();
                break;
            case 0:
                bank.exit();
                break;
        }
    }

    private static void chooseActionInitMenu(int action) {
        switch (action) {
            case 1:
                if (!isLoggedIn) {
                    bank.createAccount();
                } else {
                    bank.printBalance();
                }
                break;
            case 2:
                if (!isLoggedIn) {
                    isLoggedIn = bank.loginAccount();
                } else {
                    isLoggedIn = bank.logoutAccount();
                }
                break;
            case 0:
                bank.exit();
                break;
            default:
                System.out.println("Wrong option");
        }
    }

    private static void argsParse(String[] args) {
        for (int i = 0; i < args.length; i++){
            if ("-fileName".equals(args[i])) {
                dbFilename = args[i + 1];
            }
        }
    }
}