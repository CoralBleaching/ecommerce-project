/*
 * SQL Pagination Without OFFSET - CC-4.0 by Stephan Sokolow
 * https://creativecommons.org/licenses/by-sa/4.0/
 */

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
            System.out.println(query);

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
                    searchText, resultsPerPage, pageNumber, true);

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
