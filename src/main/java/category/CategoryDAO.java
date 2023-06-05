package category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import transactions.TransactionResult;
import utils.DatabaseUtil;

public class CategoryDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH, DB_CLASS_NAME = "org.sqlite.JDBC";

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
}
