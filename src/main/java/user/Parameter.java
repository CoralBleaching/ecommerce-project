/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package user;

/**
 *
 * @author renat
 */
public enum Parameter {
    Email("email"),
    User("user"),
    Username("username"),
    Name("name"),
    Password("password"),
    Street("street");

    private final String name;

    private Parameter(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
