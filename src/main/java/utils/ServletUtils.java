package utils;

public class ServletUtils {
    public static Integer getIntegerParameter(String value) {
        return (value != null) ? Integer.valueOf(value) : null;
    }

    public static Float getFloatParameter(String value) {
        return (value != null) ? Float.valueOf(value) : null;
    }

    public static Boolean getBooleanParameter(String value) {
        return (value != null) ? Boolean.valueOf(value) : null;
    }
}
