package user;

import transactions.TransactionResult;

public record UserFetch(TransactionResult resultValue, User user) {
    public boolean wasSuccessful() {
        return resultValue == TransactionResult.Successful;
    }
}