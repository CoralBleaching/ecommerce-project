package utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DatabaseUtil {
    private static final String PROPERTIES_FILE = "config.properties";
    private static final String DB_PATH_PROPERTY = "database.path";

    public enum WhitelistedDomains {
        ViteReactTsApp("http://localhost:5173");

        private final String url;

        WhitelistedDomains(String url) {
            this.url = url;
        }

        public String get() {
            return url;
        }
    }

    public String getDatabasePath() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(inputStream);
            return properties.getProperty(DB_PATH_PROPERTY);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
