package utils;

// TODO: export this to react app?
public enum Parameter {
    Categories("categories"),
    Category("category"),
    CategoryId("categoryId"),
    Description("description"),
    Email("email"),
    Hotness("hotness"),
    IsCategoryEditing("isCategoryUpdate"),
    IsFromStoreFront("isFromStoreFront"),
    IsPictureUpdate("isPictureUpdate"),
    IsProductEditing("isProductEditing"),
    Name("name"),
    NewSubcategoryName("newSubcategoryName"),
    NewSubcategoryDescription("newSubcategoryDescription"),
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
    SubcategoryCategoryId("subcategoryCategoryId"),
    SubcategoryDescription("subcategoryDescription"),
    SubcategoryId("subcategoryId"),
    SubcategoryName("subcategoryName"),
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
