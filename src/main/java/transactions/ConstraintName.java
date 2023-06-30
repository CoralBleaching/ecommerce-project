package transactions;

public enum ConstraintName {
    City("Address.id_city"),
    Street("Address.street"),
    Email("email"),
    Password("password"),
    User("Address.id_user"),
    Username("username"),
    Zipcode("Address.zipcode"),
    ;

    private final String name;

    private ConstraintName(String name) {
        this.name = name;
    }

    public TransactionResult getTransactionResult() {
        switch (this) {
            case City -> {
                return TransactionResult.MissingCity;
            }
            case Email -> {
                return TransactionResult.InvalidEmail;
            }
            case Password -> {
                return TransactionResult.InvalidPassword;
            }
            case Street -> {
                return TransactionResult.AddressNotFound;
            }
            case User -> {
                return TransactionResult.UserUnknownError;
            }
            case Username -> {
                return TransactionResult.InvalidUsername;
            }
            case Zipcode -> {
                return TransactionResult.MissingZipcode;
            }
            default -> throw new AssertionError(this.name());
        }
    }

    public static ConstraintName fromString(String value) {
        for (ConstraintName constraintName : ConstraintName.values()) {
            if (constraintName.name.equalsIgnoreCase(value)) {
                return constraintName;
            }
        }
        return null; // TODO: improve
    }

    public String getName() {
        return name;
    }
}
