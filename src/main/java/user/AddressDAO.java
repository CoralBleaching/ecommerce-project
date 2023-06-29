package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transactions.TransactionResult;
import utils.DatabaseUtil;

public class AddressDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH,
            DB_CLASS_NAME = "org.sqlite.JDBC";

    // TODO: incomplete
    public static AddressesFetch getAddressesByUserId(int user_id) {
        List<Address> addresses = new ArrayList<>();
        try {
            Class.forName(DB_CLASS_NAME);
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL)) {
                try (var stmt = conn.prepareStatement(
                        "select City.name as city, Address.street as street, "
                                + "Address.number as number, Address.zipcode as zipcode, "
                                + "Address.district as district, State.name as state, "
                                + "Country.name as country"
                                + "from Address, City, State, Country where "
                                + "Address.id_user = ? and "
                                + "Adress.id_city = City.id_city and "
                                + "City.id_state = State.id_state and "
                                + "State.id_country = Country.id_country")) {
                    stmt.setInt(1, user_id);
                    try (var stmt_res = stmt.executeQuery()) {
                        while (stmt_res.next()) {
                            addresses.add(new Address(
                                    stmt_res.getString("city"),
                                    stmt_res.getString("state"),
                                    stmt_res.getString("country"),
                                    stmt_res.getString("street"),
                                    stmt_res.getString("number"),
                                    stmt_res.getString("zipcode"),
                                    stmt_res.getString("district"),
                                    stmt_res.getString("label")));
                        }
                        return new AddressesFetch(TransactionResult.Successful, addresses);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            return new AddressesFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new AddressesFetch(TransactionResult.AddressNotFound, null);
        }
    }
}
