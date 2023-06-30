package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transactions.ConstraintName;
import transactions.TransactionResult;
import utils.DatabaseUtil;
import static utils.DatabaseUtil.CONSTRAINT_ERROR_MSG;

public class AddressDAO {
    private static final DatabaseUtil databaseUtil = new DatabaseUtil();
    private static final String DATABASE_PATH = databaseUtil.getDatabasePath(),
            DB_FULL_URL = "jdbc:sqlite:" + DATABASE_PATH,
            DB_CLASS_NAME = "org.sqlite.JDBC";

    public record AddressFetch(TransactionResult resultValue, Address address) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public record AddressesFetch(TransactionResult resultValue, List<Address> addresses) {
        public boolean wasSuccessful() {
            return resultValue == TransactionResult.Successful;
        }
    }

    public record CountriesFetch(TransactionResult resultValue, List<Country> countries) {
        public record State(String name, List<String> cities) {
        }

        public record Country(String name, List<State> states) {
        }
    }

    public static TransactionResult registerAddress(Integer userId, Integer cityId,
            String street, String number, String zipcode, String disctrict, String label) {
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "insert into Address (\n"
                    + "    id_user, id_city, street, number, zipcode, district, label\n"
                    + ") values (?, ?, ?, ?, ?, ?, ?);";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, cityId);
                stmt.setString(3, street);
                stmt.setString(4, number);
                stmt.setString(5, zipcode);
                stmt.setString(6, disctrict);
                stmt.setString(7, label);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    return TransactionResult.Successful;
                }
                return TransactionResult.ProblemRegisteringAddress;
            }
        } catch (ClassNotFoundException exc) {
            return TransactionResult.DatabaseConnectionError;
        } catch (SQLException exc) {
            boolean isConstraintError = exc.getMessage().startsWith(CONSTRAINT_ERROR_MSG);

            if (isConstraintError) {
                var constraint = DatabaseUtil.getConstraintString(exc.getMessage());
                return ConstraintName.fromString(constraint).getTransactionResult();
            }

            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return res;
        }
    }

    public static AddressesFetch getAddressesByUserId(int user_id) {
        List<Address> addresses = new ArrayList<>();
        try {
            Class.forName(DB_CLASS_NAME);
            String query = "select Address.id_address as id, "
                    + "City.name as city, Address.street as street, "
                    + "Address.number as number, Address.zipcode as zipcode, "
                    + "Address.district as district, State.name as state, "
                    + "Country.name as country"
                    + "from Address, City, State, Country where "
                    + "Address.id_user = ? and "
                    + "Adress.id_city = City.id_city and "
                    + "City.id_state = State.id_state and "
                    + "State.id_country = Country.id_country";
            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user_id);
                try (var stmt_res = stmt.executeQuery()) {
                    while (stmt_res.next()) {
                        addresses.add(new Address(
                                stmt_res.getInt("id"),
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
        } catch (ClassNotFoundException ex) {
            return new AddressesFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException ex) {
            return new AddressesFetch(TransactionResult.AddressNotFound, null);
        }
    }

    public static CountriesFetch getCountryStateCityInfo() {
        try {
            Class.forName(DB_CLASS_NAME);
            String countryQueryString = "select id_country, name from Country;";
            String stateQueryString = "select id_state, name from State where id_country = ?;";
            String cityQueryString = "select name from City where id_state = ?;";

            try (Connection conn = DriverManager.getConnection(DB_FULL_URL);
                    var country_stmt = conn.prepareStatement(countryQueryString);
                    var state_stmt = conn.prepareStatement(stateQueryString);
                    var city_stmt = conn.prepareStatement(cityQueryString);
                    var country_res = country_stmt.executeQuery();) {
                List<CountriesFetch.Country> countries = new ArrayList<>();
                while (country_res.next()) {
                    int countryId = country_res.getInt("id_country");
                    String countryName = country_res.getString("name");
                    List<CountriesFetch.State> states = new ArrayList<>();

                    state_stmt.setInt(1, countryId);
                    try (var state_res = state_stmt.executeQuery()) {
                        while (state_res.next()) {
                            int stateId = state_res.getInt("id_state");
                            String stateName = state_res.getString("name");
                            List<String> cities = new ArrayList<>();

                            city_stmt.setInt(1, stateId);
                            try (var city_res = city_stmt.executeQuery()) {
                                while (city_res.next()) {
                                    String cityName = city_res.getString("name");
                                    cities.add(cityName);
                                }
                            }

                            states.add(new CountriesFetch.State(stateName, cities));
                        }
                    }

                    countries.add(new CountriesFetch.Country(countryName, states));
                }

                return new CountriesFetch(TransactionResult.Successful, countries);
            }
        } catch (ClassNotFoundException exc) {
            return new CountriesFetch(TransactionResult.DatabaseConnectionError, null);
        } catch (SQLException exc) { // TODO: improve?
            var res = TransactionResult.Miscellaneous;
            res.appendToMessage(exc.getMessage());
            return new CountriesFetch(res, null);
        }
    }

}
