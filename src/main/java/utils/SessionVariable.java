package utils;

public enum SessionVariable {
    Categories("categories"),
    Category("category"),
    CategoryId("categoryId"),
    IsPictureUpdate("isPictureUpdate"),
    IsProductUpdate("isProductUpdate"),
    PictureData("pictureData"),
    PictureId("pictureId"),
    PictureInfos("pictureInfos"),
    Product("product"),
    Products("products"),
    SubcategoryId("subcategoryId"),
    User("user"),
    ;

    private final String name;

    private SessionVariable(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
