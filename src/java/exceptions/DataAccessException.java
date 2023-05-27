/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;

/**
 *
 * @author renat
 */
public class DataAccessException extends Exception {
    private final ExceptionType type;

    public DataAccessException(ExceptionType type) {
        this.type = type;
    }
    
    public DataAccessException(ConstraintName constraint) {
        switch (constraint) {
            case Username -> type = ExceptionType.InvalidUsername;
            case Password -> type = ExceptionType.InvalidPassword;
            case Email ->type = ExceptionType.InvalidEmail;
            default -> throw new java.lang.IllegalArgumentException();
        }
    }
    
    public void appendToMessage(String append) {
        type.appendToMessage(append);
    }
    
    @Override
    public String getMessage() {
        return type.getMessage();
    }
        
}
