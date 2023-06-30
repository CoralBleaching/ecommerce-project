package user;

public class User {

    public final Integer userId;
    public final String name,
            username,
            password,
            email;
    public final boolean isAdmin;

    public User(Integer userId, String name, String username, String password, String email, boolean isAdmin) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
    }

}
