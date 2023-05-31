/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package transactions;

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
    
    public TransactionResult getTransactionResult() {
        switch (this) {
            case Username -> { return TransactionResult.InvalidUsername; }
            case Password -> { return TransactionResult.InvalidPassword; }
            case Email -> { return TransactionResult.InvalidEmail; }
            default -> throw new AssertionError(this.name());
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
}
