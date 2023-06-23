package utils;

// TODO: export this to react app?
public enum Parameter {
    Category("category"),
    Email("email"),
    Name("name"),
    PageNumber("pageNumber"),
    Password("password"),
    PictureId("idPicture"),
    Products("products"),
    ProductId("idProduct"),
    ProductOrdering("orderBy"),
    ResultsPerPage("resultsPerPage"),
    SearchTerms("searchText"),
    Street("street"),
    Subcategory("subcategory"),
    User("user"),
    Username("username"),;

    private final String name;

    private Parameter(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
