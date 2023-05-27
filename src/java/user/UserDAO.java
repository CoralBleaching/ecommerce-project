/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import exceptions.ConstraintName;
import exceptions.DataAccessException;
import exceptions.ExceptionType;
import java.io.File;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author renato
 */
public class UserDAO {
    private static final String CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART =
        "CHECK constraint failed";
    
    public static boolean registerUser(User user, Address address) 
        throws DataAccessException {
        try {
            Class.forName("org.sqlite3.Driver");
            String url = "jbdc:sqlite:" + new File(
                "database", "ieeecommerce-db.db"
            ).getPath();
            try (Connection conn = DriverManager.getConnection(url)) {
            
                var user_stmt = conn.prepareStatement(
                        "insert into User ("
                            + "name, username, password, email"
                            + ") values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
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
                        address_stmt.setString(5,address.getStreet());
                        address_stmt.setString(6, address.getNumber());
                        address_stmt.setString(7, address.getZipcode());
                        address_stmt.setString(8, address.getDistrict());
                        

                        return address_stmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new DataAccessException(ExceptionType.DatabaseConnectionError);
        } catch (SQLException ex) {
            String[] message = ex.getMessage().split(": ");
            
            if (message[0].equals(CHECK_CONSTRAINT_ERROR_MSG_FIRST_PART)) {
                throw new DataAccessException(ConstraintName.valueOf(message[1]));
            } else {
                var DA_exc = new DataAccessException(ExceptionType.Miscellaneous);
                DA_exc.appendToMessage(ex.getMessage());
                throw DA_exc;
            }
        }
        return false;
    }
    
    public static boolean validateLogin(String username, String password) 
        throws DataAccessException {
        try {
            Class.forName("org.sqlite3.Driver");
            String url = "jbdc:sqlite:" + new File(
                "database", "ieeecommerce-db.db"
            ).getPath();
            try (Connection conn = DriverManager.getConnection(url)) {
                try (var stmt = conn.prepareStatement(
                    "select username, password from User"
                        + "where username = ? and password = ?")) {
                    try (var stmt_res = stmt.executeQuery()) {
                        if (stmt_res.next()) {
                            stmt.close();
                            stmt_res.close();
                            return true;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new DataAccessException(ExceptionType.DatabaseConnectionError);
        } catch (SQLException ex) {
            throw new DataAccessException(ExceptionType.UserNotFound);
        }
        return false;
    }
    
    public static Address getAddressByUserId(int user_id) throws DataAccessException {
        Address address = null;
        try {
            Class.forName("org.sqlite3.Driver");
            String url = "jbdc:sqlite:" + new File(
                "database", "ieeecommerce-db.db"
            ).getPath();
            try (Connection conn = DriverManager.getConnection(url)) {
                try (var stmt = conn.prepareStatement(
                    "select City.name as city, Address.street as street, "
                        + "Address.number as number, Address.zipcode as zipcode, "
                        + "Address.district as district, State.name as state, "
                        + "Country.name as country"
                    + "from Address, City, State, Country where"
                        + "Address.id_user = ? and "
                        + "Adress.id_city = City.id_city and "
                        + "City.id_state = State.id_state and "
                        + "State.id_country = Country.id_country")) {
                    stmt.setInt(1, user_id);
                    try (var stmt_res = stmt.executeQuery()){
                        if (stmt_res.next()) {
                            address = new Address(
                                stmt_res.getString("city"),
                                stmt_res.getString("state"),
                                stmt_res.getString("country"),
                                stmt_res.getString("street"),
                                stmt_res.getString("number"),
                                stmt_res.getString("zipcode"),
                                stmt_res.getString("district")
                            );
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new DataAccessException(ExceptionType.DatabaseConnectionError);
        } catch (SQLException ex) {
            throw new DataAccessException(ExceptionType.AddressNotFound);
        }
        return address;
    }
    
    public static User getUserByLoginNoAddress(String username, String password) 
        throws DataAccessException {
        User user = null;
        try {
            Class.forName("org.sqlite3.Driver");
            String url = "jbdc:sqlite:" + new File(
                "database", "ieeecommerce-db.db"
            ).getPath();
            try (Connection conn = DriverManager.getConnection(url)) {
                try (var stmt = conn.prepareStatement(
                    "select id_user, name, username, email, is_admin from User"
                        + "where username = ? and password = ?")) {
                    stmt.setString(1, username);
                    stmt.setString(2,password);
                    try (var stmt_res = stmt.executeQuery()){
                        if (stmt_res.next()) {
                            user = new User(
                                stmt_res.getInt("id_user"),
                                stmt_res.getString("name"),
                                stmt_res.getString("username"),
                                null,
                                stmt_res.getString("email"),
                                null,
                                stmt_res.getBoolean("is_admin")
                            );
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new DataAccessException(ExceptionType.DatabaseConnectionError);
        } catch (SQLException ex) {
            throw new DataAccessException(ExceptionType.UserNotFound);
        }
        return user;
    }
    
    public static User getUserByLoginFull(String username, String password) 
        throws DataAccessException {
        User user = getUserByLoginNoAddress(username, password);
        user.setAddress(getAddressByUserId(user.getUserId()));
        return user;
    }
    
    
}
