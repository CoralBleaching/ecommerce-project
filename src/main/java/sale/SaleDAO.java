package sale;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import transactions.DataFetch;
import transactions.TransactionResult;
import utils.DatabaseUtil;

public class SaleDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DB_FULL_URL = databaseUtil.getDatabaseUrl(),
            DB_CLASS_NAME = "org.postgresql.Driver";

    public static class SaleFetch extends DataFetch {
        public final Sale sale;

        public SaleFetch(TransactionResult resultValue, Sale sale) {
            super(resultValue);
            this.sale = sale;
        }
    }

    public static class SalesFetch extends DataFetch {
        public final List<Sale> sales;

        public SalesFetch(TransactionResult resultValue, List<Sale> sales) {
            super(resultValue);
            this.sales = sales;
        }
    }

    public static TransactionResult registerSale(int userId, List<Integer> priceIds, List<Integer> quantities) {
        try {
            Class.forName(DB_CLASS_NAME);
            String saleQuery = "insert into Sale (id_user) values (?);";
            String soldQuery = "insert into Sold (id_sale, id_price, quantity) " +
                    "values (?, ?, ?);";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var saleStmt = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);
                    var soldStmt = conn.prepareStatement(soldQuery);) {
                saleStmt.setInt(1, userId);
                saleStmt.executeUpdate();
                try (var generatedKeys = saleStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int saleId = generatedKeys.getInt(1);
                        for (int i = 0; i < priceIds.size(); i++) {
                            soldStmt.setInt(1, saleId);
                            soldStmt.setInt(2, priceIds.get(i));
                            soldStmt.setInt(3, quantities.get(i));
                            soldStmt.addBatch();
                        }
                        int[] affectedRows = soldStmt.executeBatch();
                        for (int i : affectedRows) {
                            if (i == 0 || i == PreparedStatement.EXECUTE_FAILED) {
                                return TransactionResult.ProblemProcessingAnOrder;
                            }
                        }
                        return TransactionResult.Successful;
                    }
                }
                return TransactionResult.UserNotFound;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            return TransactionResult.ProblemProcessingAnOrder;
        }
    }

    public static SaleFetch getSaleById(int saleId) {
        try {
            Class.forName(DB_CLASS_NAME);
            String saleQuery = "select id_user, timestamp from Sale where id_sale = ?";
            String pricesQuery = "select id_price, quantity from Sold where id_sale = ?;";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var saleStmt = conn.prepareStatement(saleQuery);
                    var pricesStmt = conn.prepareStatement(pricesQuery);) {
                saleStmt.setInt(1, saleId);
                try (var saleRes = saleStmt.executeQuery()) {
                    if (saleRes.next()) {
                        String timestamp = saleRes.getString("timestamp");
                        int userId = saleRes.getInt("id_user");

                        List<Integer> priceIds = new ArrayList<>();
                        List<Integer> quantities = new ArrayList<>();
                        pricesStmt.setInt(1, saleId);
                        try (var pricesRes = pricesStmt.executeQuery()) {
                            while (pricesRes.next()) {
                                priceIds.add(pricesRes.getInt("id_price"));
                                quantities.add(pricesRes.getInt("quantity"));
                            }
                        }

                        Sale sale = new Sale(saleId, userId, timestamp, quantities, priceIds);
                        return new SaleFetch(TransactionResult.Successful, sale);
                    }

                    return new SaleFetch(TransactionResult.InvalidSaleId, null);
                }
            }
        } catch (ClassNotFoundException exc) {
        } catch (SQLException exc) {
        }
        return new SaleFetch(TransactionResult.DatabaseConnectionError, null);
    }

    public static SalesFetch getSalesByUserId(int userId) {
        try {
            Class.forName(DB_CLASS_NAME);
            String saleQuery = "select id_sale, timestamp from Sale where id_user = ?";
            String pricesQuery = "select id_price, quantity from Sold where id_sale = ?;";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var saleStmt = conn.prepareStatement(saleQuery);
                    var pricesStmt = conn.prepareStatement(pricesQuery);) {
                saleStmt.setInt(1, userId);
                try (var saleRes = saleStmt.executeQuery()) {
                    List<Sale> sales = new ArrayList<>();
                    while (saleRes.next()) {
                        String timestamp = saleRes.getString("timestamp");
                        int saleId = saleRes.getInt("id_sale");

                        List<Integer> priceIds = new ArrayList<>();
                        List<Integer> quantities = new ArrayList<>();
                        pricesStmt.setInt(1, saleId);
                        try (var pricesRes = pricesStmt.executeQuery()) {
                            while (pricesRes.next()) {
                                priceIds.add(pricesRes.getInt("id_price"));
                                quantities.add(pricesRes.getInt("quantity"));
                            }
                        }

                        sales.add(new Sale(saleId, userId, timestamp, quantities, priceIds));
                    }

                    return new SalesFetch(TransactionResult.InvalidSaleId, sales);
                }
            }
        } catch (ClassNotFoundException exc) {
        } catch (SQLException exc) {
        }
        return new SalesFetch(TransactionResult.DatabaseConnectionError, null);
    }

}
