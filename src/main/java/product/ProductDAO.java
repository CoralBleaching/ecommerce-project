package product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transactions.TransactionResult;
import utils.DatabaseUtil;

public class ProductDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH, DB_CLASS_NAME = "org.sqlite.JDBC";

    public record ProductFetch(TransactionResult resultValue, Product product) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public record ProductsFetch(TransactionResult resultValue, List<Product> products) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public static ProductsFetch getAllProducts() {
        try {
            Class.forName(DB_CLASS_NAME);
            String sql = "select " +
                    "p.id_product, p.id_picture, p.name, p.description, p.stock, " +
                    "c.name as category, sc.name as subcategory, pr.value as price " +
                    "FROM Product p " +
                    "inner join Subcategory sc on p.id_subcategory = sc.id_subcategory " +
                    "inner join Category c on sc.id_category = c.id_category " +
                    "inner join (select id_product, max(timestamp) as max_timestamp " +
                    "      from Price " +
                    "      group by id_product) as latest_price on p.id_product = latest_price.id_product " +
                    "inner join Price pr on latest_price.id_product = pr.id_product and latest_price.max_timestamp = pr.timestamp";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var prod_stmt = conn.prepareStatement(sql);
                    var prod_res = prod_stmt.executeQuery()) {

                List<Product> products = new ArrayList<>();

                while (prod_res.next()) {
                    Product product = new Product();
                    product.setIdProduct(prod_res.getInt("id_product"));
                    product.setIdPicture(prod_res.getInt("id_picture"));
                    product.setName(prod_res.getString("name"));
                    product.setDescription(prod_res.getString("description"));
                    product.setStock(prod_res.getInt("stock"));
                    product.setPrice(prod_res.getFloat("price"));
                    product.setCategory(prod_res.getString("category"));
                    product.setSubcategory(prod_res.getString("subcategory"));

                    products.add(product);
                }

                return new ProductsFetch(TransactionResult.Successful, products);
            }
        } catch (ClassNotFoundException ex) {
            return new ProductsFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new ProductsFetch(TransactionResult.ProductNotFound, null);
        }
    }
}
