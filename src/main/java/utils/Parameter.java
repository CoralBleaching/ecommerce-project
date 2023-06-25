package utils;

// TODO: export this to react app?
public enum Parameter {
    Categories("categories"),
    Category("category"),
    Description("description"),
    Email("email"),
    Hotness("hotness"),
    IsPictureUpdate("isPictureUpdate"),
    IsProductEditing("isProductEditing"),
    Name("name"),
    PageNumber("pageNumber"),
    Password("password"),
    PictureFile("pictureFile"),
    PictureId("idPicture"),
    Price("price"),
    Product("product"),
    Products("products"),
    ProductId("idProduct"),
    ProductOrdering("orderBy"),
    ResultsPerPage("resultsPerPage"),
    SearchTerms("searchText"),
    Stock("stock"),
    Street("street"),
    Subcategory("subcategory"),
    User("user"),
    Username("username"),
    ;

    private final String name;

    private Parameter(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
