import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Database {
    private static String url;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Database.url = url;
    }

    public static void initDB(String dbFilename) {
        setUrl("jdbc:sqlite:./" + dbFilename);
        createDbFile(dbFilename);
        createCardTable();
    }

    private static Connection openConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }
        return connection;
    }

    private static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }
    }

    private static void createDbFile(String dbFilename) {
        Path dbPath = Paths.get(String.format("./%s", dbFilename));
        if (!Files.exists(dbPath)) {
            try {
                Files.createFile(dbPath);
            } catch (IOException e) {
                System.out.printf("An IOException occurred: %s%n", e.getMessage());
            }
        }
    }

    private static void createCardTable() {
        Connection connection = Database.openConnection();

        String query = "CREATE TABLE IF NOT EXISTS card ("
                + "id INTEGER primary key,"
                + "number TEXT,"
                + "pin TEXT,"
                + "balance INTEGER DEFAULT 0)";

        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
        } catch (SQLException e){
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }

        Database.closeConnection(connection);
    }

    public static boolean cardExistsInDB(String cardNumber){
        Connection connection = Database.openConnection();
        String query = String.format("SELECT COUNT(*) FROM card WHERE number = %s", cardNumber);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)){
            boolean exists = resultSet.getBoolean(1);
            if (exists) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            Database.closeConnection(connection);
        }
        return false;
    }

    public static void transferMoney(Account account,int amount,String whereTo){
        Connection connection = Database.openConnection();
            withdrawMoney(account, amount);
            String query = String.format("UPDATE card SET balance=balance+%s WHERE number=%s",
                    amount, whereTo);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
                connection.commit();
            } catch (SQLException e) {
                System.out.printf("An SQLException occurred: %s%n", e.getMessage());
            }
    }

    public static void withdrawMoney(Account account, int amount){
        Connection connection = Database.openConnection();
        String query = String.format("UPDATE card SET balance=balance-%s WHERE number=%s",
                amount, Account.getCardNumber());
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
            connection.commit();
        }
        catch(SQLException e){
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }
    }

    public static void addIncome(Account account,int income){
        Connection connection = Database.openConnection();
        account.setBalance(Account.getBalance()+income);
        String query = String.format("UPDATE card SET balance=balance+%s WHERE number=%s",
                income, Account.getCardNumber());
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
            connection.commit();
        }
        catch(SQLException e){
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }
    }

    public static void closeAccount(Account account){
        Connection connection = Database.openConnection();
        String query = String.format("DELETE FROM card WHERE (number=%s)",
                Account.getCardNumber());
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
            connection.commit();
        }
        catch (SQLException e){
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }

        Database.closeConnection(connection);
    }

    public static void saveAccount(Account account) {
        Connection connection = Database.openConnection();

        String query = String.format("INSERT INTO card (number, pin) values (%s, %s)",
                Account.getCardNumber(), account.getPin());

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            connection.commit();
        } catch (SQLException e) {
            System.out.printf("An SQLException occurred: %s%n", e.getMessage());
        }

        Database.closeConnection(connection);
    }

    public static Account loadAccount(String number, int pin){
        Connection connection = Database.openConnection();

        String query = String.format("SELECT number, pin, balance FROM card WHERE number = %s and pin = %s",
                number, pin);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)){
            boolean exists = resultSet.next();

            if (exists) {
                Account account = new Account();
                account.setCardNumber(resultSet.getString("number"));
                account.setPin(Integer.parseInt(resultSet.getString("pin")));
                account.setBalance(resultSet.getInt("balance"));
                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Database.closeConnection(connection);
        return null;
    }
}
