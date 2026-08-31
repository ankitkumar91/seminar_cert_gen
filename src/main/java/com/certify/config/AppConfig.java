package com.certify.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {
    public static final int CERT_WIDTH = 1920;
    public static final int CERT_HEIGHT = 1358;
    public static final int CERT_DIM_TOLERANCE = 2;
    public static final long MAX_UPLOAD_BYTES = 12L * 1024 * 1024;

    public static final String SESSION_USER = "authUser";
    public static final String CSRF = "csrfToken";
    public static final String FLASH = "flashMessage";
    public static final String FLASH_TYPE = "flashType";

    private AppConfig() {}

    public static Path dataDir() {
        String override = System.getProperty("certify.data.dir");
        if (override == null || override.isBlank()) {
            override = System.getenv("CERTIFY_DATA_DIR");
        }
        if (override == null || override.isBlank()) {
            String base = System.getProperty("catalina.base", System.getProperty("user.dir"));
            override = Paths.get(base, "certify-data").toString();
        }
        return Paths.get(override).toAbsolutePath().normalize();
    }

    public static Path uploadsDir() {
        return dataDir().resolve("uploads");
    }

    public static Path dbFile() {
        return dataDir().resolve("certify");
    }
}
