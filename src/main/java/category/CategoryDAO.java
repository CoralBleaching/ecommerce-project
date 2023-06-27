package category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;

import transactions.TransactionResult;
import utils.DatabaseUtil;

public class CategoryDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH, DB_CLASS_NAME = "org.sqlite.JDBC",
            TRIGGER_MESSAGE = "[SQLITE_CONSTRAINT_TRIGGER]";

    public record CategoryFetch(TransactionResult resultValue, Category category) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public record CategoriesFetch(TransactionResult resultValue, List<Category> categories) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public record CategoryRegistration(TransactionResult resultValue, Integer Id) {
    };

    public static CategoriesFetch getAllCategories() {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (var cat_stmt = conn.prepareStatement("select id_category, name, description from Category;",
                        Statement.RETURN_GENERATED_KEYS);
                        var subcat_stmt = conn
                                .prepareStatement("select id_subcategory, name, description from Subcategory"
                                        + " where id_category = ?;");
                        var cat_res = cat_stmt.executeQuery()) {
                    ArrayList<Category> categories = new ArrayList<>();
                    while (cat_res.next()) {
                        Category category = new Category(
                                cat_res.getInt("id_category"),
                                cat_res.getString("name"),
                                cat_res.getString("description"));

                        ArrayList<Subcategory> subcategories = new ArrayList<>();
                        subcat_stmt.setInt(1, category.getIdCategory());
                        try (var subcat_res = subcat_stmt.executeQuery()) {
                            while (subcat_res.next()) {
                                subcategories.add(new Subcategory(
                                        subcat_res.getInt("id_subcategory"),
                                        subcat_res.getString("name"),
                                        subcat_res.getString("description")));
                            }
                        }
                        category.setSubcategories(subcategories);
                        categories.add(category);
                    }
                    return new CategoriesFetch(TransactionResult.Successful, categories);
                }
            }
        } catch (ClassNotFoundException ex) {
            return new CategoriesFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            System.out.println(ex);
            return new CategoriesFetch(TransactionResult.CategoryNotFound, null);
        }
    }

    public static CategoryRegistration registerCategory(String name, String description) {
        try {
            Class.forName(DB_CLASS_NAME);
            boolean hasName = name != null;
            boolean hasDescription = description != null;
            boolean hasBoth = hasName && hasDescription;
            String query = "insert into Category (" +
                    (hasName ? "name" : "") +
                    (hasBoth ? ", " : "") +
                    (hasDescription ? "description" : "") +
                    ") values (?" + (hasBoth ? ", ?);" : ");");
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                int res = stmt.executeUpdate();
                try (var generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer categoryId = generatedKeys.getInt(1);
                        return new CategoryRegistration(TransactionResult.Successful, categoryId);
                    }
                    return new CategoryRegistration(TransactionResult.Miscellaneous, null); // TODO: improve
                }
            }
        } catch (ClassNotFoundException exc) {
            return new CategoryRegistration(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException exc) { // TODO: improve (constraint checking)
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return new CategoryRegistration(res, null);
        }
    }

    public static TransactionResult registerSubcategory(String name, String description, Integer categoryId) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "insert into Subcategory (name, description, id_category) " +
                    "values (?, ?, ?);";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setInt(3, categoryId);
                int res = stmt.executeUpdate();
                if (res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.Miscellaneous; // TODO: improve
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) { // TODO: improve (constraint checking)
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static TransactionResult updateCategory(Integer categoryId, String name, String description) {
        try {
            Class.forName(DB_CLASS_NAME);
            boolean hasName = name != null;
            boolean hasDescription = description != null;
            boolean hasBoth = hasName && hasDescription;
            String query = "update Category set " +
                    (hasName ? "name" : "") + " = ? " +
                    (hasBoth ? ", " : "") +
                    (hasDescription ? "description" : "") + " = ? " +
                    " where id_category = ?;";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setInt(3, categoryId);
                int res = stmt.executeUpdate();
                if (res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.CategoryNotFound;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) { // TODO: improve (constraint checking)
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static TransactionResult updateSubcategory(Integer subcategoryId, String name, String description,
            Integer categoryId) {
        try {
            Class.forName(DB_CLASS_NAME);
            int hasName = (name != null) ? 1 : 0;
            int hasDescription = (description != null) ? 1 : 0;
            int hasCategoryId = (categoryId != null) ? 1 : 0;
            int numArgs = hasName + hasDescription + hasCategoryId;
            String query = "update Subcategory set " +
                    (hasName == 1 ? "name" : "") + " = ? " +
                    (numArgs > 1 ? ", " : "") +
                    (hasDescription == 1 ? "description" : "") + " = ? " +
                    (numArgs > 2 ? ", " : "") +
                    (hasCategoryId == 1 ? "id_category" : "") + " = ? " +
                    " where id_subcategory = ?;";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setInt(3, categoryId);
                stmt.setInt(4, subcategoryId);

                int res = stmt.executeUpdate();
                if (res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.CategoryNotFound;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) { // TODO: improve (constraint checking)
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static TransactionResult removeCategoryById(Integer categoryId) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "delete from Category where id_category = ?";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, categoryId);
                int res = stmt.executeUpdate();
                if (res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.CategoryNotFound;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            boolean isTrigger = exc.getMessage().startsWith(TRIGGER_MESSAGE);
            if (isTrigger) {
                return TransactionResult.CategoryNotEmpty;
            }
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static TransactionResult removeSubcategoryById(Integer subcategoryId) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "delete from Subcategory where id_subcategory = ?";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, subcategoryId);
                int res = stmt.executeUpdate();
                if (res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.SubcategoryNotFound;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            boolean isTrigger = exc.getMessage().startsWith(TRIGGER_MESSAGE);
            if (isTrigger) {
                return TransactionResult.SubcategoryNotEmpty;
            }
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }
}
