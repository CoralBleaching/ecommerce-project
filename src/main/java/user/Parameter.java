package user;

// TODO: export this to react app?
public enum Parameter {
    Category("category"),
    Email("email"),
    User("user"),
    Username("username"),
    Name("name"),
    Password("password"),
    Street("street"),
    Subcategory("subcategory");

    private final String name;

    private Parameter(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
