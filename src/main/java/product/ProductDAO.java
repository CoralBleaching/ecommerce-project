package product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import transactions.TransactionResult;
import utils.DatabaseUtil;

// TODO: ability to remove evaluations?

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

    public static Integer getProductCount(Integer idCategory, Integer idSubcategory,
            String searchText) {
        try {
            Class.forName(DB_CLASS_NAME);

            String query = "select count(*) \n" +
                    "from Product p, Category c, Subcategory s\n" +
                    "where\n" +
                    "    p.id_subcategory = s.id_subcategory and\n" +
                    "    c.id_category = s.id_category " +
                    ((idSubcategory != null) ? "and\n    s.id_subcategory = ? " : "") +
                    ((idCategory != null) ? "and\n    c.id_category = ? " : "");
            if (searchText != null) {
                query += "\n    and (\n";
                String[] searchTerms = searchText.split(" ");
                for (String term : searchTerms) {
                    query += "        p.name like '%" + term + "%' or \n";
                    query += "        p.description like '%" + term + "%' or \n";
                    query += "        c.name like '%" + term + "%' or \n";
                    query += "        s.name like '%" + term + "%' or \n";
                }
                query = query.substring(0, query.lastIndexOf("or")) + "\n";
                query += "    )\n";
            }
            query += ";";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);) {

                if (idSubcategory != null) {
                    stmt.setInt(1, idSubcategory);
                    if (idCategory != null) {
                        stmt.setInt(2, idCategory);
                    }
                } else {
                    if (idCategory != null) {
                        stmt.setInt(1, idCategory);
                    }
                }

                try (var res = stmt.executeQuery();) {
                    if (res.next()) {
                        return res.getInt(1);
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc);
            return null;
        }
        return null;
    }

    private static String getOrderToken(String orderBy) {
        Order order = Order.getFromString(orderBy);
        String orderToken;
        switch (order) {
            case PriceAscending:
                orderToken = "price asc";
                break;
            case PriceDescending:
                orderToken = "price desc";
                break;
            case Newest:
                orderToken = "p.timestamp desc";
                break;
            case Oldest:
                orderToken = "p.timestamp asc";
                break;
            default:
                orderToken = "p.hotness desc";
        }
        return orderToken;
    }

    private static String generateProductQuery(
            Integer idCategory, Integer idSubcategory, String orderBy,
            String searchText, Integer resultsPerPage, Integer pageNumber,
            Boolean verbose) {
        String query = "select\n" +
                "    p.id_product, p.id_picture, p.name, p.description, p.stock, p.hotness, p.timestamp,\n" +
                "    c.name as category, s.name as subcategory, pr.value as price\n" +
                "from Product p, Category c, Subcategory s, Price pr, (\n" +
                "        select id_product, max(timestamp) as max_timestamp \n" +
                "        from Price \n" +
                "        group by id_product\n" +
                "    ) as latest_price\n" +
                "where\n" +
                "    p.id_subcategory = s.id_subcategory and\n" +
                "    c.id_category = s.id_category and\n" +
                ((idSubcategory != null) ? "    s.id_subcategory = ? and \n" : "") +
                ((idCategory != null) ? "    c.id_category = ? and \n" : "") +
                "    p.id_product = latest_price.id_product and\n" +
                "    latest_price.id_product = pr.id_product and\n" +
                "    latest_price.max_timestamp = pr.timestamp\n";
        if (searchText != null) {
            query += "    and (\n";
            String[] searchTerms = searchText.split(" ");
            for (String term : searchTerms) {
                query += "        p.name like '%" + term + "%' or \n";
                query += "        p.description like '%" + term + "%' or \n";
                query += "        category like '%" + term + "%' or \n";
                query += "        subcategory like '%" + term + "%' or \n";
            }
            query = query.substring(0, query.lastIndexOf("or")) + "\n";
            query += "    )\n";
        }
        String orderToken = getOrderToken(orderBy);
        query += "order by " + orderToken;
        if (resultsPerPage != null && pageNumber != null) {
            Integer offset = (pageNumber - 1) * resultsPerPage;
            query += " limit " + Integer.toString(offset) + ", " + Integer.toString(resultsPerPage);
        }
        query += ";";
        if (verbose != null && verbose == true)
            System.out.println(query);
        return query;
    }

    public static ProductsFetch getProducts(Integer idCategory, Integer idSubcategory,
            String orderBy, String searchText, Integer resultsPerPage, Integer pageNumber) {
        try {
            Class.forName(DB_CLASS_NAME);

            String query = generateProductQuery(idCategory, idSubcategory, orderBy,
                    searchText, resultsPerPage, pageNumber, false);

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var prod_stmt = conn.prepareStatement(query)) {
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
                        Product product = new Product(
                                prod_res.getInt("id_product"),
                                prod_res.getInt("id_picture"),
                                prod_res.getInt("stock"),
                                prod_res.getInt("hotness"),
                                prod_res.getString("timestamp"),
                                prod_res.getFloat("price"),
                                prod_res.getString("category"),
                                prod_res.getString("subcategory"),
                                prod_res.getString("name"),
                                prod_res.getString("description"));
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

    public static ProductFetch getProduct(Integer idProduct) {
        try {
            Class.forName(DB_CLASS_NAME);

            String query = "select\n" +
                    "    p.id_product, p.id_picture, p.name, p.description, p.stock, p.hotness, p.timestamp,\n" +
                    "    c.name as category, s.name as subcategory, pr.value as price\n" +
                    "from Product p, Category c, Subcategory s, Price pr, (\n" +
                    "        select id_product, max(timestamp) as max_timestamp \n" +
                    "        from Price \n" +
                    "        group by id_product\n" +
                    "    ) as latest_price\n" +
                    "where\n" +
                    "    p.id_product = " + Integer.toString(idProduct) + " and\n" +
                    "    s.id_subcategory = p.id_subcategory and\n" +
                    "    c.id_category = s.id_category and\n" +
                    "    p.id_product = latest_price.id_product and\n" +
                    "    latest_price.id_product = pr.id_product and\n" +
                    "    latest_price.max_timestamp = pr.timestamp\n" +
                    ";";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var prod_stmt = conn.prepareStatement(query);
                    var prod_res = prod_stmt.executeQuery();) {
                if (prod_res.next()) {
                    Product product = new Product(
                            prod_res.getInt("id_product"),
                            prod_res.getInt("id_picture"),
                            prod_res.getInt("stock"),
                            prod_res.getInt("hotness"),
                            prod_res.getString("timestamp"),
                            prod_res.getFloat("price"),
                            prod_res.getString("category"),
                            prod_res.getString("subcategory"),
                            prod_res.getString("name"),
                            prod_res.getString("description"));
                    return new ProductFetch(TransactionResult.Successful, product);
                }
                return new ProductFetch(TransactionResult.ProductNotFound, null);
            }
        } catch (ClassNotFoundException ex) {
            return new ProductFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new ProductFetch(TransactionResult.ProductNotFound, null);
        }
    }

    public static void updatePrice(Integer productId, Float newValue, Connection conn)
            throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName(DB_CLASS_NAME);
            conn = DriverManager.getConnection(DB_FULL_URL);
        }
        String query = "insert into Price (id_product, value) values (?, ?);";
        var stmt = conn.prepareStatement(query);
        stmt.setInt(1, productId);
        stmt.setFloat(2, newValue);
        stmt.executeUpdate();
    }

    public static TransactionResult updateProduct(
            Integer productId, Integer pictureId, String name, String description, Integer category,
            Integer subcategory, Integer stock, Integer hotness, Float price) {
        try {
            Class.forName(DB_CLASS_NAME);

            String subcategoryQuery = "select Subcategory.id_subcategory as id_subcategory from Subcategory, Category \n"
                    + "where Category.id_category = " + Integer.toString(category) + "\n"
                    + " and Subcategory.id_subcategory = " + Integer.toString(subcategory) + "\n"
                    + " and Subcategory.id_category = Category.id_category;";
            String productQuery = "update Product set " +
                    "name = ?,  description = ?, stock = ?, hotness = ?, id_subcategory = ?, id_picture = ? " +
                    "where id_product = " + Integer.toString(productId) + ";";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var subcategory_stmt = conn.prepareStatement(subcategoryQuery);
                    var subcat_res = subcategory_stmt.executeQuery();
                    var product_stmt = conn.prepareStatement(productQuery);) {
                subcat_res.next(); // TODO: implement check
                int id_subcategory = subcat_res.getInt("id_subcategory");
                product_stmt.setString(1, name);
                product_stmt.setString(2, description);
                product_stmt.setInt(3, stock);
                product_stmt.setInt(4, hotness);
                product_stmt.setInt(5, id_subcategory);
                product_stmt.setInt(6, pictureId);

                int product_res = product_stmt.executeUpdate();
                if (product_res > 0) {
                    updatePrice(productId, price, conn);
                    return TransactionResult.Successful;
                }

                return TransactionResult.Miscellaneous;
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            return TransactionResult.InvalidUsername; // TODO: create value
        }
    }

    public static TransactionResult registerProduct(
            String name, String description, Integer category, Integer subcategory,
            Integer pictureId, Integer stock, Integer hotness, Float price) {
        try {
            Class.forName(DB_CLASS_NAME);

            String subcategoryQuery = "select Subcategory.id_subcategory as id_subcategory from Subcategory, Category \n"
                    + "where Category.id_category = " + Integer.toString(category) + "\n"
                    + " and Subcategory.id_subcategory = " + Integer.toString(subcategory) + "\n"
                    + " and Subcategory.id_category = Category.id_category;";
            String productQuery = "insert into Product\n" +
                    "    (name, description, stock, hotness, id_subcategory, id_picture)\n" +
                    "values (?, ?, ?, ?, ?, ?)\n" +
                    ";";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var subcategory_stmt = conn.prepareStatement(subcategoryQuery);
                    var subcat_res = subcategory_stmt.executeQuery();
                    var product_stmt = conn.prepareStatement(productQuery, Statement.RETURN_GENERATED_KEYS);) {
                subcat_res.next(); // TODO: implement check
                int id_subcategory = subcat_res.getInt("id_subcategory");
                product_stmt.setString(1, name);
                product_stmt.setString(2, description);
                product_stmt.setInt(3, stock);
                product_stmt.setInt(4, hotness);
                product_stmt.setInt(5, id_subcategory);
                product_stmt.setInt(6, pictureId);

                int product_res = product_stmt.executeUpdate();
                if (product_res > 0) {
                    try (var generatedKeys = product_stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int productId = generatedKeys.getInt(1);
                            updatePrice(productId, price, conn);
                            return TransactionResult.Successful;
                        }
                    }
                }
                return TransactionResult.Miscellaneous;
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            TransactionResult res = TransactionResult.Miscellaneous;
            res.appendToMessage(ex.toString());
            return res; // TODO: create value
        }
    }

    public static TransactionResult removeProductById(int idProduct) {
        try {
            Class.forName(DB_CLASS_NAME);
            String productQuery = "delete from Product where " +
                    "id_product = " + Integer.toString(idProduct) + ";";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var product_stmt = conn.prepareStatement(productQuery);) {
                int product_res = product_stmt.executeUpdate();
                if (product_res > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.Miscellaneous;
            }
        } catch (ClassNotFoundException ex) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException ex) {
            return TransactionResult.ProductNotFound; // TODO: adjust
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

    // TODO: introduce pagination
    public static List<PictureInfo> getPictureInfos() {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "select id_picture, name from Picture";
            List<PictureInfo> pictureInfos = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);
                    var res = stmt.executeQuery()) {
                while (res.next()) {
                    pictureInfos.add(new PictureInfo(
                            res.getInt("id_picture"),
                            res.getString("name")));
                }
            }
            return pictureInfos;
        } catch (ClassNotFoundException exc) {
            return null;
        } catch (SQLException exc) {
            return null;
        }
    }

    public static String getPictureById(Integer id) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "select data from Picture where id_picture = " + Integer.toString(id) + ";";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query);
                    var res = stmt.executeQuery()) {
                if (res.next()) {
                    return res.getString("data");
                } else {
                    return null;
                }
            }
        } catch (ClassNotFoundException exc) {
            return null;
        } catch (SQLException exc) {
            return null;
        }
    }

    public static void registerPicture(String name, String data) {
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    PreparedStatement stmt = conn.prepareStatement("insert into Picture (name, data) values (?, ?)")) {
                stmt.setString(1, name);
                stmt.setString(2, data);
                stmt.executeUpdate();
            }
        } catch (ClassNotFoundException exc) {

        } catch (SQLException e) {

        }
    }

    public static void updatePicture(Integer pictureId, String name, String data) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query;
            if (data == null) {
                query = "update Picture set name = ? where id_picture = ?;";
            } else {
                query = "update Picture set name = ?, data = ? where id_picture = ? ;";
            }
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                if (data == null) {
                    stmt.setInt(2, pictureId);
                } else {
                    stmt.setString(2, data);
                    stmt.setInt(3, pictureId);
                }
                stmt.executeUpdate();
            }
        } catch (ClassNotFoundException exc) {

        } catch (SQLException e) {

        }
    }

}
