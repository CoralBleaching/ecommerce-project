package user;

import java.util.List;

import transactions.TransactionResult;

public record AddressesFetch(TransactionResult resultValue, List<Address> addresses) {
    public boolean wasSuccessful() {
        return resultValue == TransactionResult.Successful;
    }
}