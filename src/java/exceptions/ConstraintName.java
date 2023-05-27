/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package exceptions;

/**
 *
 * @author renat
 */
public enum ConstraintName {
    Username("username"),
    Password("password"),
    Email("email");
    
    private final String name;
    private ConstraintName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
