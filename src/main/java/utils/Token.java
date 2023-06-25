package utils;

public enum Token {
    EditPicturesTitle("Edit pictures"),
    RegisterPictureTitle("Upload the image file"),
    RegisterProductForm("Product information"),
    RegisterProductTitle("Register the product"),
    RegisterUserForm("Registration form"),
    RegisterUserTitle("Registration"),
    SelectPictureForEdit("Select picture for editing"),
    UpdateProductTitle("Edit the product information"),
    ;

    private final String name;

    private Token(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
