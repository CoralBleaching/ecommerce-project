/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

/**
 *
 * @author renato
 */
public class User {

    private int userId;
    private String 
            name, 
            username, 
            password, 
            email;
    private Address address;
    private boolean isAdmin;

    public User() {}
    
    public User(int userId, String name, String username, String password, String email, Address address, boolean isAdmin) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.isAdmin = isAdmin;
    }
    
    public User(User other) {
        this.userId = other.userId;
        this.name = other.name;
        this.username = other.username;
        this.password = other.password;
        this.email = other.email;
        this.address = new Address(other.address);
        this.isAdmin = other.isAdmin;
    }
    
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name=" + name + 
                ", username=" + username +
                ", password=" + password + 
                ", email=" + email +
                ", address=" + address + 
                ", isAdmin=" + isAdmin +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    
}