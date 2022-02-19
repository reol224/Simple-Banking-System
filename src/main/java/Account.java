import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Account {
    private static String cardNumber;
    private int pin;
    private static int balance;
    public static boolean isLoggedIn;

    {
        if (isLoggedIn) {
            Main.bank.initMenu();
            int action = Integer.parseInt(Main.scanner.nextLine());
            chooseActionInitMenu(action);
        } else {
            Main.bank.loginMenu();
            int action = Integer.parseInt(Main.scanner.nextLine());
            chooseActionLoginMenu(action);
        }
        //int action = Integer.parseInt(scanner.nextLine());
        //chooseActionInitMenu(action);
    }

    static void chooseActionLoginMenu(int action){
        switch (action) {
            case 1:
                if(isLoggedIn){
                    Main.bank.printBalance();
                }
                break;
            case 2:
                System.out.println("Enter income:");
                Scanner scanner = new Scanner(System.in);
                int income = scanner.nextInt();
                Database.addIncome(Bank.currentAccount,income);
                System.out.println("Income was added!");
                break;
            case 3: {
                Scanner scannerX = new Scanner(System.in);
                System.out.println("Transfer");
                System.out.println("Enter card number");
                String cardNumber = scannerX.nextLine();
                if (cardNumber.equals(getCardNumber())) {
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
                if (amount > getBalance()) {
                    System.out.println("Not enough money!");
                    break;
                }
                else {
                    //Database.withdrawMoney(bank.currentAccount,amount);
                    Database.transferMoney(Bank.currentAccount, amount, cardNumber);
                    Bank.updatedBalance(getBalance(), amount);
                    System.out.println("Success!");
                }
            }
            break;
            case 4:
                Database.closeAccount(Bank.currentAccount);
                Main.bank.logoutAccount();
                break;
            case 5:
                Main.bank.logoutAccount();
                break;
            case 0:
                Main.bank.exit();
                break;
        }
    }

    static void chooseActionInitMenu(int action) {
        switch (action) {
            case 1:
                if (isLoggedIn) {
                    Main.bank.createAccount();
                } else {
                    Main.bank.printBalance();
                }
                break;
            case 2:
                if (isLoggedIn) {
                    Main.bank.loginAccount();
                } else {
                    Main.bank.logoutAccount();
                }
                break;
            case 0:
                Main.bank.exit();
                break;
            default:
                System.out.println("Wrong option");
        }
    }
    public Account() {

    }

    public static Account createNewAccount(){
        Account account = new Account();
        cardNumber = generateCardNumber();
        account.pin = generatePin();
        balance = 0;

        return account;
    }

    private static String generateCardNumber() {
        String cardNumber1 = Bank.IIN + generateAccountIdentifier();
        return cardNumber1 + generateCheckSum(cardNumber1);
    }

    private static Long generateAccountIdentifier() {
        return ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
    }

    private static int generatePin() {
        return ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    private static int generateCheckSum(String cardNumber) {
        int sum = getLuhnAlgorithmSum(cardNumber);
        return (100 - sum) % 10;
    }

    public static int getLuhnAlgorithmSum(String cardNumber) {
        //Split cardNumber into List of digits
        String[] split = cardNumber.split("\\B");
        List<Integer> digits = new ArrayList<>();
        for (String s : split) {
            Integer integer = Integer.valueOf(s);
            digits.add(integer);
        }

        // Multiply odd digits by 2
        for (int i = 0; i < digits.size(); i++) {
            if ((i + 1) % 2 != 0) {
                digits.set(i, digits.get(i) * 2);
            }
        }

        //Subtract 9 from numbers > 9
        IntStream.range(0, digits.size()).filter(i -> digits.get(i) > 9).forEach(i -> digits.set(i, digits.get(i) - 9));

        //Sum up control number
        return digits.stream().mapToInt(i -> i).sum();
    }

    public void printBalance() {
        System.out.println("Balance: " + balance);
    }

    public static String getCardNumber() {
        return cardNumber;
    }

    public int getPin() {
        return pin;
    }

    public static int getBalance() {
        return balance;
    }

    public void setCardNumber(String cardNumber) {
        Account.cardNumber = cardNumber;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setBalance(int balance) {
        Account.balance = balance;
    }
    public long generateCardNumberMINE(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(400000);
        for(int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));

        }
        int checkDigit = getCheckDigit(sb.toString());
        sb.append(checkDigit);

        return Long.parseLong(sb.toString());
    }

    public  int generatePinMINE(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++)
            sb.append(random.nextInt(10));
        return Integer.parseInt(sb.toString());
    }

    private int getCheckDigit(String number) {

        // Get the sum of all the digits, however we need to replace the value
        // of every other digit with the same digit multiplied by 2. If this
        // multiplication yields a number greater than 9, then add the two
        // digits together to get a single digit number.
        //
        // The digits we need to replace will be those in an even position for
        // card numbers whose length is an even number, or those is an odd
        // position for card numbers whose length is an odd number. This is
        // because the Luhn algorithm reverses the card number, and doubles
        // every other number starting from the second number from the last
        // position.
        int sum = 0;
        int remainder = (number.length() + 1) % 2;
        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == remainder) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }

        // The check digit is the number required to make the sum a multiple of
        // 10.
        int mod = sum % 10;

        return ((mod == 0) ? 0 : 10 - mod);
    }
}
