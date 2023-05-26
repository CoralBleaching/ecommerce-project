package database.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {

    public static void runStatementsFromFile(Connection conn, String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            StringBuilder sb = new StringBuilder();
            Statement statement = conn.createStatement();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());

                if (line.matches("\\s*")) {
                    statement.execute(sb.toString());
                    sb.setLength(0);
                }
            }

            if (sb.length() > 0) {
                statement.execute(sb.toString());
            }

        } catch (FileNotFoundException err) {
            System.out.println("File \"" + fileName + "\" not found.");
        } catch (IOException err) {
            System.out.println("Problem reading from file.");
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.class.path",
                    System.getProperty("java.class.path") + ":./lib/sqlite-jdbc-3.41.2.1.jar");

            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }

        String url = "jdbc:sqlite:" + new File("database", "ieeecommerce-db.db").getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(url)) {
            runStatementsFromFile(conn, "database/scripts/create-database.sql");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}