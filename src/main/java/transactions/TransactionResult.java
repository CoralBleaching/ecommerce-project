package transactions;

public enum TransactionResult {
    AddressNotFound("Address wasn't registed for the user."),
    CategoryNotFound("The product category isn't available."),
    DatabaseConnectionError("There was a problem connecting to the server."),
    InvalidEmail("The entered email is not valid. Valid example: something@provider.com"),
    InvalidPassword("The entered password is not valid. Need at least 8 characters, at least 1 letter and 1 number."),
    InvalidUsername("The entered username is not valid. Only letters, numbers and spaces are allowed."),
    Miscellaneous("Database operation error: "),
    ProductNotFound("Sorry, we don't have this particular product."),
    Successful(""),
    UserNotFound("Username not registered.");

    private String message;

    private TransactionResult(String message) {
        this.message = message;
    }

    public void appendToMessage(String append) {
        message += append;
    }

    public String getMessage() {
        return message;
    }

    public boolean wasSuccessful() {
        return this == Successful;
    }

}
