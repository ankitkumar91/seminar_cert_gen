package com.certify;

import com.certify.config.AppConfig;
import com.certify.db.Database;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.nio.file.Files;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Files.createDirectories(AppConfig.dataDir());
            Files.createDirectories(AppConfig.uploadsDir());
            Database.init();
            sce.getServletContext().log("Certify data dir: " + AppConfig.dataDir());
        } catch (Exception e) {
            throw new IllegalStateException("Application startup failed", e);
        }
    }
}
