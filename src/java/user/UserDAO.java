/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import java.io.File;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author renato
 */
public class UserDAO {
    
    public static boolean registerUser(User user, Address address) {
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
        } catch (ClassNotFoundException | SQLException ex) { 
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex); 
        }
        return false;
    }
    
    public static boolean validateLogin(String username, String password) {
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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static Address getAddressByUserId(int user_id) {
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
                                stmt_res.getString("distritc")
                            );
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }
    
    public static User getUserByLoginNoAddress(String username, String password) {
        User user = null;
        try {
            Class.forName("org.sqlite3.Driver");
            String url = "jbdc:sqlite:" + new File(
                "database", "ieeecommerce-db.db"
            ).getPath();
            try (Connection conn = DriverManager.getConnection(url)) {
                try (var stmt = conn.prepareStatement(
                    "select name, username, email, is_admin from User"
                        + "where username = ? and password = ?")) {
                    stmt.setString(1, username);
                    stmt.setString(2,password);
                    try (var stmt_res = stmt.executeQuery()){
                        if (stmt_res.next()) {
                            user = new User(
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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    
}
