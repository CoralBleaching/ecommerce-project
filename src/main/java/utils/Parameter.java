package utils;

// TODO: export this to react app?
public enum Parameter {
    Category("category"),
    Email("email"),
    LastValue("lastValue"),
    Name("name"),
    PageNumber("pageNumber"),
    Password("password"),
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
