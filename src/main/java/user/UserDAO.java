package user;

import transactions.ConstraintName;
import transactions.TransactionResult;
import utils.DatabaseUtil;
import static utils.DatabaseUtil.CONSTRAINT_ERROR_MSG;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DB_FULL_URL = databaseUtil.getDatabaseUrl(),
            DB_CLASS_NAME = "org.postgresql.Driver";

    public record SignUpFetch(TransactionResult resultValue, User user) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public static SignUpFetch registerUser(String name, String username,
            String password, String email) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {

                var user_stmt = conn.prepareStatement(
                        "insert into \"User\" ("
                                + "name, username, password, email"
                                + ") values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                user_stmt.setString(1, name);
                user_stmt.setString(2, username);
                user_stmt.setString(3, password);
                user_stmt.setString(4, email);

                user_stmt.executeUpdate();

                try (var generatedKeys = user_stmt.getGeneratedKeys();) {
                    if (generatedKeys.next()) {
                        Integer id_user = generatedKeys.getInt(1);
                        User user = new User(
                                id_user,
                                name,
                                username,
                                password,
                                email,
                                false);
                        return new SignUpFetch(TransactionResult.Successful, user);
                    }
                }
                return new SignUpFetch(TransactionResult.UserUnknownError, null);
            }
        } catch (ClassNotFoundException exc) {
            return new SignUpFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException exc) {
            boolean isConstraintError = exc.getMessage().startsWith(CONSTRAINT_ERROR_MSG);

            if (isConstraintError) {
                var constraint = DatabaseUtil.getConstraintString(exc.getMessage());
                return new SignUpFetch(
                        ConstraintName.fromString(constraint).getTransactionResult(),
                        null);
            }

            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return new SignUpFetch(res, null);
        }
    }

    public static TransactionResult updateUser(User user) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {

                try (var user_stmt = conn.prepareStatement(
                        "update \"User\" set "
                                + "name = ?, username = ?, password = ?, email = ? "
                                + "where id_user = ?;")) {
                    user_stmt.setString(1, user.name);
                    user_stmt.setString(2, user.username);
                    user_stmt.setString(3, user.password);
                    user_stmt.setString(4, user.email);
                    user_stmt.setInt(5, user.userId);

                    user_stmt.executeUpdate();

                    return TransactionResult.Successful;
                }
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            boolean isConstraintError = exc.getMessage().startsWith(CONSTRAINT_ERROR_MSG);

            if (isConstraintError) {
                var constraint = DatabaseUtil.getConstraintString(exc.getMessage());
                return ConstraintName.fromString(constraint).getTransactionResult();
            }

            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static TransactionResult deleteByLogin(int userId) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "delete from \"User\""
                                + " where username = ?;")) {
                    stmt.setInt(1, userId);
                    if (stmt.executeUpdate() > 0) {
                        return TransactionResult.Successful;
                    }
                    return TransactionResult.UserUnknownError;
                }
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static UserFetch getUserByLogin(String username, String password) {
        User user = null;
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (var stmt = conn.prepareStatement(
                        "select id_user, name, username, email, is_admin from \"User\""
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
                        return new UserFetch(TransactionResult.UserNotFound, null);
                    }
                }
            }
        } catch (ClassNotFoundException exc) {
            return new UserFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException exc) {
            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return new UserFetch(res, null);
        }
    }

}
