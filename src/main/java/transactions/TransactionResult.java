package transactions;

public enum TransactionResult {
    AddressNotFound("Address wasn't registed for the user."),
    CategoryNotEmpty("Cannot remove category that contains subcategories."),
    CategoryNotFound("The product category doesn't exist."),
    DatabaseConnectionError("There was a problem connecting to the server."),
    EvaluationNotFound("There's no evaluation for this product or product info is incorrect."),
    InvalidEmail("The entered email is not valid. Valid example: something@provider.com"),
    InvalidPassword("The entered password is not valid. Need at least 8 characters, at least 1 letter and 1 number."),
    InvalidUsername("The entered username is not valid. Only letters, numbers and spaces are allowed."),
    Miscellaneous("Operation error: "),
    ProductNotFound("Sorry, we don't have this particular product."),
    SubcategoryNotFound("The product subcategory doesn't exist."),
    SubcategoryNotEmpty("Cannot remove subcategory that contains products."),
    Successful(""),
    UserUnknownError("Unexpected error."),
    UserNotFound("Username not registered."),
    ;

    private final String baseMsg;
    private String message;

    private TransactionResult(String message) {
        this.message = message;
        baseMsg = message;
    }

    public void appendToMessage(String append) {
        message = baseMsg + append;
    }

    public String getMessage() {
        return message;
    }

    public boolean wasSuccessful() {
        return this == Successful;
    }

}
