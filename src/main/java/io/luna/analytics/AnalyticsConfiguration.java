package io.luna.analytics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class AnalyticsConfiguration {
    private final Properties properties;
    private static final String CONFIG_FILE_NAME = "analytics.properties";

    AnalyticsConfiguration() throws IOException {
        properties = new Properties();

        try (InputStream is = AnalyticsConfiguration.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (is == null) {
                throw new FileNotFoundException("Configuration file not found at src/main/resources: " + CONFIG_FILE_NAME);
            } else {
                properties.load(is);
            }
        }
    }

    boolean isFileStorageEnabled() {
        return Boolean.parseBoolean(properties.getProperty("file.enabled", "true"));
    }

    String getFilePath() {
        return properties.getProperty("file.path");
    }

    boolean isDatabaseEnabled() {
        return Boolean.parseBoolean(properties.getProperty("database.enabled", "false"));
    }

    String getDatabaseUrl() {
        return properties.getProperty("database.url", "");
    }

    String getDatabaseUser() {
        return properties.getProperty("database.user", "");
    }

    String getDatabasePassword() {
        return properties.getProperty("database.password", "");
    }

}
