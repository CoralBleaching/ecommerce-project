package utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DatabaseUtil {
    private static final String PROPERTIES_FILE = "config.properties";
    private static final String DB_URL_PROPERTY = "database.url";
    private static final int OFFSET_BEGIN = 2,
            OFFSET_END = 1;

    public static final String CONSTRAINT_ERROR_MSG = "[SQLITE_CONSTRAINT",
            MSG_SEPARATOR = ":";

    public enum WhitelistedDomains {
        ViteReactTsAppDesktop("http://localhost:5173"),
        // ViteReactTsAppDocker("http://localhost");
        ViteReactTsAppDocker("http://localhost:3000");
        // ViteReactTsAppDocker("http://ecommerce-frontend:3000");

        private final String url;

        WhitelistedDomains(String url) {
            this.url = url;
        }

        public String get() {
            return url;
        }
    }

    public String getDatabaseUrl() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(inputStream);
            return properties.getProperty(DB_URL_PROPERTY);
        } catch (Exception e) { // TODO: improve
            System.out.println(e);
        }
        return null;
    }

    public static String getConstraintString(String message) {
        int length = message.length();
        int index = message.lastIndexOf(MSG_SEPARATOR, length) + OFFSET_BEGIN;
        return message.substring(index, length - OFFSET_END);
    }
}
