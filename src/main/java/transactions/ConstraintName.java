package transactions;

public enum ConstraintName {
    username,
    password,
    email;

    public TransactionResult getTransactionResult() {
        switch (this) {
            case username -> {
                return TransactionResult.InvalidUsername;
            }
            case password -> {
                return TransactionResult.InvalidPassword;
            }
            case email -> {
                return TransactionResult.InvalidEmail;
            }
            default -> throw new AssertionError(this.name());
        }
    }
}
