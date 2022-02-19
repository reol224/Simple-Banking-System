import java.util.Scanner;

public class Main {
    static Bank bank = new Bank();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++){
            if ("-fileName".equals(args[i])) {
                Bank.dbFilename = args[i + 1];
            }
        }

        if (Account.isLoggedIn) {
            Main.bank.initMenu();
            int action = Integer.parseInt(Main.scanner.nextLine());
            Account.chooseActionInitMenu(action);
        } else {
            Main.bank.loginMenu();
            int action = Integer.parseInt(Main.scanner.nextLine());
            Account.chooseActionLoginMenu(action);
        }

    }
}