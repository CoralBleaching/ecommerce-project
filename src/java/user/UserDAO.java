/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import java.util.Map;

/**
 *
 * @author renato
 */
public class UserDAO {
    public boolean registerUser(
            String username,
            String password,
            String name,
            String email,
            Map address,
            boolean isAdmin) {
        try {
            Class.forName("org.sqlite3.Driver");
            User newUser = new User(name,
                username, password, email, address, isAdmin);
            
        }
        return true;
    }
}
