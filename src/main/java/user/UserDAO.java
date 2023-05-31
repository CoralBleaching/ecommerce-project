/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import transactions.ConstraintName;
import transactions.TransactionResult;
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
    private static final String CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART = "CHECK constraint failed",
            CHECK_CONSTRAINT_ERROR_MSG_SEPARATOR = ": ";

    public static TransactionResult registerUser(User user, Address address) {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:\\ieeecommerce-db.db";
            try (Connection conn = DriverManager.getConnection(url)) {

                var user_stmt = conn.prepareStatement(
                        "insert into User ("
                                + "name, username, password, email"
                                + ") values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                user_stmt.setString(1, user.getName());
                user_stmt.setString(2, user.getUsername());
                user_stmt.setString(3, user.getPassword());
                user_stmt.setString(4, user.getEmail());

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
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            String[] message = ex.getMessage().split(CHECK_CONSTRAINT_ERROR_MSG_SEPARATOR);

            if (message[0].equals(CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART)) {
                return ConstraintName.valueOf(message[1]).getTransactionResult();
            } else {
                var resultValue = TransactionResult.Miscellaneous;
                resultValue.appendToMessage(ex.getMessage());
                return resultValue;
            }
        }
        return TransactionResult.Successful;
    }

    public static TransactionResult validateLogin(String username, String password) {
        String url = "jdbc:sqlite:C:\\ieeecommerce-db.db";
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(url)) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select username, password from User"
                                + " where username = ? and password = ?;")) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet stmt_res = stmt.executeQuery()) {
                        if (stmt_res.next()) {
                            stmt.close();
                            stmt_res.close();
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
        return TransactionResult.Successful;
    }

    public record AddressFetch(TransactionResult resultValue, Address address) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public static AddressFetch getAddressByUserId(int user_id) {
        Address address = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:\\ieeecommerce-db.db";
            try (Connection conn = DriverManager.getConnection(url)) {
                try (var stmt = conn.prepareStatement(
                        "select City.name as city, Address.street as street, "
                                + "Address.number as number, Address.zipcode as zipcode, "
                                + "Address.district as district, State.name as state, "
                                + "Country.name as country"
                                + "from Address, City, State, Country where "
                                + "Address.id_user = ? and "
                                + "Adress.id_city = City.id_city and "
                                + "City.id_state = State.id_state and "
                                + "State.id_country = Country.id_country")) {
                    stmt.setInt(1, user_id);
                    try (var stmt_res = stmt.executeQuery()) {
                        if (stmt_res.next()) {
                            address = new Address(
                                    stmt_res.getString("city"),
                                    stmt_res.getString("state"),
                                    stmt_res.getString("country"),
                                    stmt_res.getString("street"),
                                    stmt_res.getString("number"),
                                    stmt_res.getString("zipcode"),
                                    stmt_res.getString("district"));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            return new AddressFetch(TransactionResult.DatabaseConnectionError, new Address());
        } catch (SQLException ex) {
            return new AddressFetch(TransactionResult.AddressNotFound, new Address());
        }
        return new AddressFetch(TransactionResult.Successful, address);
    }

    public record UserFetch(TransactionResult resultValue, User user) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public static UserFetch getUserByLoginNoAddress(String username, String password) {
        User user = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:\\ieeecommerce-db.db";
            try (Connection conn = DriverManager.getConnection(url)) {
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
                                    null,
                                    stmt_res.getBoolean("is_admin"));
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
        return new UserFetch(TransactionResult.Successful, user);
    }

    public static UserFetch getUserByLoginFull(String username, String password) {
        UserFetch userFetch = getUserByLoginNoAddress(username, password);
        System.out.println("[getUserByLoginFull] " + userFetch);
        if (userFetch.wasSuccessful()) {
            AddressFetch addressFetch = getAddressByUserId(
                    userFetch.user().getUserId());
            if (addressFetch.wasSuccessful()) {
                User user = new User(userFetch.user());
                user.setAddress(new Address(addressFetch.address()));
                return new UserFetch(userFetch.resultValue(), user);
            }
            return new UserFetch(
                    addressFetch.resultValue(), userFetch.user());
        }
        return userFetch;
    }

}
