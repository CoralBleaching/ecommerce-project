package user;

import transactions.ConstraintName;
import transactions.TransactionResult;
import utils.DatabaseUtil;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author renato
 */
public class UserDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART = "[SQLITE_CONSTRAINT_CHECK] A CHECK constraint failed (CHECK constraint failed",
            CHECK_CONSTRAINT_ERROR_MSG_SEPARATOR = ": ",
            DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH,
            DB_CLASS_NAME = "org.sqlite.JDBC";

    public static TransactionResult registerUser(User user, Address address) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {

                var user_stmt = conn.prepareStatement(
                        "insert into User ("
                                + "name, username, password, email"
                                + ") values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                user_stmt.setString(1, user.getName());
                user_stmt.setString(2, user.getUsername());
                user_stmt.setString(3, user.getPassword());
                user_stmt.setString(4, user.getEmail());

                user_stmt.executeUpdate();

                if (address == null) {
                    return TransactionResult.Successful;
                }

                int affectedRows = user_stmt.executeUpdate();
                if (affectedRows > 0) {
                    var generatedKeys = user_stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id_user = generatedKeys.getInt(1);
                        var address_stmt = conn.prepareStatement(
                                "insert into Address ("
                                        + "id_user, id_city, street, "
                                        + "number, zipcode, district"
                                        + ") "
                                        + "values (?, ("
                                        + "select id_city from City "
                                        + "where name = ? and id_state = ("
                                        + "select id_state from State "
                                        + "where name = ? and id_country = ("
                                        + "select id_country from Country "
                                        + "where name = ?"
                                        + ")"
                                        + ")"
                                        + "), "
                                        + "?, ?, ?, ?)"

                        );
                        address_stmt.setInt(1, id_user);
                        address_stmt.setString(2, address.getCity());
                        address_stmt.setString(3, address.getState());
                        address_stmt.setString(4, address.getCountry());
                        address_stmt.setString(5, address.getStreet());
                        address_stmt.setString(6, address.getNumber());
                        address_stmt.setString(7, address.getZipcode());
                        address_stmt.setString(8, address.getDistrict());

                        address_stmt.executeUpdate();
                        return TransactionResult.Successful;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            String[] message = ex.getMessage().split(CHECK_CONSTRAINT_ERROR_MSG_SEPARATOR);

            if (message[0].equals(CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART)) {
                String constraint = message[1]
                        .replace(")", "");
                constraint = constraint.substring(0, 1).toUpperCase()
                        + constraint.substring(1);
                return ConstraintName.valueOf(constraint).getTransactionResult();
            } else {
                var resultValue = TransactionResult.Miscellaneous;
                resultValue.appendToMessage(ex.getMessage());
                return resultValue;
            }
        }
        return TransactionResult.Miscellaneous;
    }

    public static TransactionResult updateUser(String oldUsername, User user, Address address) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {

                try (var user_stmt = conn.prepareStatement(
                        "update User set "
                                + "name = ?, username = ?, password = ?, email = ? "
                                + "where username = ?")) {
                    user_stmt.setString(1, user.getName());
                    user_stmt.setString(2, user.getUsername());
                    user_stmt.setString(3, user.getPassword());
                    user_stmt.setString(4, user.getEmail());
                    user_stmt.setString(5, oldUsername);

                    user_stmt.executeUpdate();

                    return TransactionResult.Successful;
                }
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            String[] message = ex.getMessage().split(CHECK_CONSTRAINT_ERROR_MSG_SEPARATOR);

            if (message[0].equals(CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART)) {
                String constraint = message[1]
                        .replace(")", "");
                constraint = constraint.substring(0, 1).toUpperCase()
                        + constraint.substring(1);
                return ConstraintName.valueOf(constraint).getTransactionResult();
            } else {
                var resultValue = TransactionResult.Miscellaneous;
                resultValue.appendToMessage(ex.getMessage());
                return resultValue;
            }
        }
    }

    public static TransactionResult validateLogin(String username, String password) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select username, password from User"
                                + " where username = ? and password = ?;")) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet stmt_res = stmt.executeQuery()) {
                        if (stmt_res.next()) {
                            return TransactionResult.Successful;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.toString());
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return TransactionResult.UserNotFound;
        }
        return TransactionResult.Miscellaneous;
    }

    public static TransactionResult deleteByLogin(String username, String password) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "delete from User"
                                + " where username = ?;")) {
                    stmt.setString(1, username);
                    // stmt.setString(2, password);
                    stmt.executeUpdate();
                    return TransactionResult.Successful;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.toString());
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return TransactionResult.UserNotFound;
        }
    }

    public static UserFetch getUserByLogin(String username, String password) {
        User user = null;
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (var stmt = conn.prepareStatement(
                        "select id_user, name, username, email, is_admin from User"
                                + " where username = ? and password = ?")) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (var stmt_res = stmt.executeQuery()) {
                        if (stmt_res.next()) {
                            user = new User(
                                    stmt_res.getInt("id_user"),
                                    stmt_res.getString("name"),
                                    stmt_res.getString("username"),
                                    null,
                                    stmt_res.getString("email"),
                                    stmt_res.getBoolean("is_admin"));
                            return new UserFetch(TransactionResult.Successful, user);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("[getUserByLoginNoAddress] " + ex);
            return new UserFetch(TransactionResult.DatabaseConnectionError, new User());
        } catch (SQLException ex) {
            System.out.println("[getUserByLoginNoAddress] " + ex);
            return new UserFetch(TransactionResult.UserNotFound, new User());
        }
        return new UserFetch(TransactionResult.UserNotFound, null);
    }

}
