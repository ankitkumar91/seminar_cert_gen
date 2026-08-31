package com.certify.db;

import com.certify.config.AppConfig;
import com.certify.model.FieldPosition;
import com.certify.model.FormField;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;
import com.certify.util.PasswordUtil;
import com.certify.util.SampleCertificateFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public final class Database {
    private static String jdbcUrl;

    private Database() {}

    public static void init() {
        try {
            Files.createDirectories(AppConfig.dataDir());
            Files.createDirectories(AppConfig.uploadsDir());
            Class.forName("org.h2.Driver");
            jdbcUrl = "jdbc:h2:file:" + AppConfig.dbFile() + ";AUTO_SERVER=TRUE;MODE=LEGACY;DB_CLOSE_DELAY=-1";
            try (Connection c = getConnection()) {
                migrate(c);
                seed(c);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialise database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    private static void migrate(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                  id IDENTITY PRIMARY KEY,
                  username VARCHAR(64) NOT NULL UNIQUE,
                  password_hash VARCHAR(120) NOT NULL,
                  display_name VARCHAR(120) NOT NULL,
                  role VARCHAR(20) NOT NULL
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS seminars (
                  id IDENTITY PRIMARY KEY,
                  title VARCHAR(255) NOT NULL,
                  description CLOB,
                  venue VARCHAR(255),
                  seminar_date DATE,
                  organizer VARCHAR(255),
                  status VARCHAR(32) NOT NULL,
                  template_relpath VARCHAR(512),
                  template_width INT,
                  template_height INT,
                  created_by BIGINT NOT NULL,
                  created_at TIMESTAMP NOT NULL,
                  updated_at TIMESTAMP NOT NULL,
                  approved_by BIGINT,
                  approved_at TIMESTAMP
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS field_positions (
                  id IDENTITY PRIMARY KEY,
                  seminar_id BIGINT NOT NULL,
                  field_key VARCHAR(40) NOT NULL,
                  x_percent DOUBLE NOT NULL,
                  y_percent DOUBLE NOT NULL,
                  width_percent DOUBLE NOT NULL,
                  font_size INT NOT NULL,
                  font_color VARCHAR(16) NOT NULL,
                  font_bold BOOLEAN NOT NULL,
                  text_align VARCHAR(10) NOT NULL,
                  UNIQUE (seminar_id, field_key)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS share_links (
                  id IDENTITY PRIMARY KEY,
                  seminar_id BIGINT NOT NULL,
                  token VARCHAR(64) NOT NULL UNIQUE,
                  expires_at TIMESTAMP NOT NULL,
                  created_at TIMESTAMP NOT NULL,
                  created_by BIGINT,
                  note VARCHAR(255)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS submissions (
                  id IDENTITY PRIMARY KEY,
                  seminar_id BIGINT NOT NULL,
                  share_link_id BIGINT,
                  full_name VARCHAR(200) NOT NULL,
                  email VARCHAR(200) NOT NULL,
                  phone VARCHAR(40),
                  institute VARCHAR(255) NOT NULL,
                  speciality VARCHAR(120) NOT NULL,
                  designation VARCHAR(80) NOT NULL,
                  created_at TIMESTAMP NOT NULL,
                  ip_address VARCHAR(80)
                )
                """);
            migrateLegacy(st);
        }
    }

    private static void migrateLegacy(Statement st) {
        trySql(st, "ALTER TABLE submissions ALTER COLUMN phone SET NULL");
        trySql(st, "ALTER TABLE submissions ADD COLUMN IF NOT EXISTS institute VARCHAR(255)");
        trySql(st, "ALTER TABLE submissions ADD COLUMN IF NOT EXISTS speciality VARCHAR(120)");
        trySql(st, "ALTER TABLE submissions ALTER COLUMN college DROP NOT NULL");
        trySql(st, "ALTER TABLE submissions ALTER COLUMN enrollment_no DROP NOT NULL");
        trySql(st, "UPDATE submissions SET institute = college WHERE (institute IS NULL OR institute = '') AND college IS NOT NULL");
        trySql(st, "UPDATE submissions SET speciality = enrollment_no WHERE (speciality IS NULL OR speciality = '') AND enrollment_no IS NOT NULL");
        trySql(st, "UPDATE field_positions SET field_key = 'institute' WHERE field_key = 'college'");
        trySql(st, "UPDATE field_positions SET field_key = 'speciality' WHERE field_key IN ('enrollmentNo', 'enrollment_no')");
    }

    private static void trySql(Statement st, String sql) {
        try {
            st.execute(sql);
        } catch (SQLException ignored) {
            // Column already matches the new schema.
        }
    }

    private static void seed(Connection c) throws Exception {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return;
            }
        }

        long adminId;
        long devId;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users (username, password_hash, display_name, role) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "admin");
            ps.setString(2, PasswordUtil.hash("Admin@123"));
            ps.setString(3, "Seminar Admin");
            ps.setString(4, "ADMIN");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                adminId = keys.getLong(1);
            }
            ps.setString(1, "developer");
            ps.setString(2, PasswordUtil.hash("Dev@123"));
            ps.setString(3, "Certificate Developer");
            ps.setString(4, "DEVELOPER");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                devId = keys.getLong(1);
            }
        }

        Instant now = Instant.now();
        long seminarId;
        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO seminars (title, description, venue, seminar_date, organizer, status,
                  template_relpath, template_width, template_height, created_by, created_at, updated_at,
                  approved_by, approved_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "National Workshop on Cloud-Native Java");
            ps.setString(2, "A one-day workshop covering servlets, JSP, and certificate automation for campus events.");
            ps.setString(3, "Main Auditorium, Institute of Technology");
            ps.setObject(4, LocalDate.now().minusDays(3));
            ps.setString(5, "Department of Computer Applications");
            ps.setString(6, SeminarStatus.APPROVED.name());
            ps.setString(7, "seminars/1/template.png");
            ps.setInt(8, AppConfig.CERT_WIDTH);
            ps.setInt(9, AppConfig.CERT_HEIGHT);
            ps.setLong(10, adminId);
            ps.setObject(11, now);
            ps.setObject(12, now);
            ps.setLong(13, devId);
            ps.setObject(14, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                seminarId = keys.getLong(1);
            }
        }

        Path template = AppConfig.uploadsDir().resolve("seminars/" + seminarId + "/template.png");
        Files.createDirectories(template.getParent());
        SampleCertificateFactory.writeDemoTemplate(template);

        FormField[] printed = {
                FormField.FULL_NAME, FormField.INSTITUTE, FormField.SPECIALITY, FormField.DESIGNATION
        };
        double[] ys = {46.5, 54.0, 59.5, 65.0};
        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO field_positions (seminar_id, field_key, x_percent, y_percent, width_percent,
                  font_size, font_color, font_bold, text_align)
                VALUES (?,?,?,?,?,?,?,?,?)
                """)) {
            for (int i = 0; i < printed.length; i++) {
                FieldPosition p = FieldPosition.defaults(seminarId, printed[i], ys[i]);
                ps.setLong(1, seminarId);
                ps.setString(2, p.getFieldKey());
                ps.setDouble(3, p.getXPercent());
                ps.setDouble(4, p.getYPercent());
                ps.setDouble(5, p.getWidthPercent());
                ps.setInt(6, p.getFontSize());
                ps.setString(7, p.getFontColor());
                ps.setBoolean(8, p.isFontBold());
                ps.setString(9, p.getTextAlign());
                ps.executeUpdate();
            }
        }

        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO share_links (seminar_id, token, expires_at, created_at, created_by, note)
                VALUES (?,?,?,?,?,?)
                """)) {
            ps.setLong(1, seminarId);
            ps.setString(2, "demo-nwcj-2026");
            ps.setObject(3, Instant.now().plus(30, ChronoUnit.DAYS));
            ps.setObject(4, now);
            ps.setLong(5, adminId);
            ps.setString(6, "Seeded demo link for local preview");
            ps.executeUpdate();
        }

        // Second seminar waiting on developer alignment
        Seminar pending = new Seminar();
        pending.setTitle("Faculty Development Programme on Secure Web Apps");
        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO seminars (title, description, venue, seminar_date, organizer, status,
                  template_relpath, template_width, template_height, created_by, created_at, updated_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Faculty Development Programme on Secure Web Apps");
            ps.setString(2, "Five-day FDP. Certificate design uploaded; awaiting field alignment.");
            ps.setString(3, "Seminar Hall B");
            ps.setObject(4, LocalDate.now().plusDays(10));
            ps.setString(5, "IQAC & CSE Department");
            ps.setString(6, SeminarStatus.PENDING_APPROVAL.name());
            ps.setString(7, "seminars/2/template.png");
            ps.setInt(8, AppConfig.CERT_WIDTH);
            ps.setInt(9, AppConfig.CERT_HEIGHT);
            ps.setLong(10, adminId);
            ps.setObject(11, now);
            ps.setObject(12, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                long pendingId = keys.getLong(1);
                Path p2 = AppConfig.uploadsDir().resolve("seminars/" + pendingId + "/template.png");
                Files.createDirectories(p2.getParent());
                SampleCertificateFactory.writePendingTemplate(p2);
            }
        }

        // Unused UUID so checkstyle on seed uniqueness is obvious
        UUID.randomUUID();
    }
}
