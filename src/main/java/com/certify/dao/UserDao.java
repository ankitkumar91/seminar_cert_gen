package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.Role;
import com.certify.model.User;
import com.certify.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public User authenticate(String username, String password) {
        String sql = """
                SELECT id, username, password_hash, display_name, role, active
                FROM users WHERE username = ?
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                if (!isActive(rs)) {
                    return null;
                }
                if (!PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    return null;
                }
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User find(long id) {
        String sql = "SELECT id, username, password_hash, display_name, role, active FROM users WHERE id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, display_name, role, active FROM users WHERE username = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAdmins() {
        List<User> list = new ArrayList<>();
        String sql = """
                SELECT id, username, password_hash, display_name, role, active
                FROM users WHERE role = 'ADMIN'
                ORDER BY active DESC, username ASC
                """;
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public int countActiveAdmins() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN' AND COALESCE(active, TRUE) = TRUE";
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long insertAdmin(String username, String displayName, String password) {
        String sql = """
                INSERT INTO users (username, password_hash, display_name, role, active)
                VALUES (?,?,?,?,TRUE)
                """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, displayName);
            ps.setString(4, Role.ADMIN.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void restoreAdmin(long id, String displayName, String password) {
        String sql = """
                UPDATE users SET password_hash=?, display_name=?, active=TRUE
                WHERE id=? AND role='ADMIN'
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(password));
            ps.setString(2, displayName);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean revokeAdmin(long id) {
        String sql = "UPDATE users SET active=FALSE WHERE id=? AND role='ADMIN' AND COALESCE(active, TRUE)=TRUE";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isActive(ResultSet rs) throws SQLException {
        boolean active = rs.getBoolean("active");
        if (rs.wasNull()) {
            return true;
        }
        return active;
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("display_name"),
                Role.valueOf(rs.getString("role")),
                isActive(rs));
    }
}
