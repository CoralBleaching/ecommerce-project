package product;

import java.sql.Connection;
import java.sql.DriverManager;
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

    public record EvaluationsFetch(TransactionResult resultValue, List<Evaluation> evaluation) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    // TODO: some form of pagination must be implemented
    public static ProductsFetch getAllProducts() {
        return getProductsByCategoryAndOrSubcategory(null, null);
    }

    public static ProductsFetch getProductsByCategoryAndOrSubcategory(Integer idCategory, Integer idSubcategory) {
        try {
            Class.forName(DB_CLASS_NAME);
            String sql = "select " +
                    "p.id_product, p.id_picture, p.name, p.description, p.stock, " +
                    "p.hotness, p.timestamp, " +
                    "c.name as category, sc.name as subcategory, pr.value as price " +
                    "from Product p " +
                    "inner join Subcategory sc on p.id_subcategory = sc.id_subcategory " +
                    ((idSubcategory != null) ? " and sc.id_subcategory = ? " : "") +
                    "inner join Category c on sc.id_category = c.id_category " +
                    ((idCategory != null) ? " and c.id_category = ? " : "") +
                    "inner join (select id_product, max(timestamp) as max_timestamp " +
                    "      from Price " +
                    "      group by id_product) as latest_price on p.id_product = latest_price.id_product " +
                    "inner join Price pr on latest_price.id_product = pr.id_product and latest_price.max_timestamp = pr.timestamp order by hotness desc;";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var prod_stmt = conn.prepareStatement(sql)) {

                if (idSubcategory != null) {
                    prod_stmt.setInt(1, idSubcategory);
                    if (idCategory != null) {
                        prod_stmt.setInt(2, idCategory);
                    }
                } else {
                    if (idCategory != null) {
                        prod_stmt.setInt(1, idCategory);
                    }
                }

                try (var prod_res = prod_stmt.executeQuery()) {

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
                        product.setHotness(prod_res.getInt("hotness"));
                        product.setTimestamp(prod_res.getString("timestamp"));

                        products.add(product);
                    }

                    return new ProductsFetch(TransactionResult.Successful, products);
                }
            }
        } catch (ClassNotFoundException ex) {
            return new ProductsFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new ProductsFetch(TransactionResult.ProductNotFound, null);
        }
    }

    public static EvaluationsFetch getEvaluationByProductId(int idProduct) {
        try {
            Class.forName(DB_CLASS_NAME);
            String queryString = "select id_sale, timestamp, review, score from Evaluation" +
                    String.format(" where id_product = %d;", idProduct);

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var eval_stmt = conn.prepareStatement(queryString);
                    var eval_res = eval_stmt.executeQuery();) {
                List<Evaluation> evaluations = new ArrayList<>();
                while (eval_res.next()) {
                    evaluations.add(new Evaluation(
                            idProduct,
                            eval_res.getInt("id_sale"),
                            eval_res.getInt("score"),
                            eval_res.getString("timestamp"),
                            eval_res.getString("review")));
                }
                return new EvaluationsFetch(TransactionResult.Successful, evaluations);
            }
        } catch (ClassNotFoundException ex) {
            return new EvaluationsFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new EvaluationsFetch(TransactionResult.EvaluationNotFound, null);
        }
    }
}
