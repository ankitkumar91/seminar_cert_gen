package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.ShareLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShareLinkDao {

    public List<ShareLink> findBySeminar(long seminarId) {
        List<ShareLink> list = new ArrayList<>();
        String sql = "SELECT * FROM share_links WHERE seminar_id = ? ORDER BY created_at DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, seminarId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public ShareLink findByToken(String token) {
        String sql = "SELECT * FROM share_links WHERE token = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ShareLink create(long seminarId, Instant expiresAt, long createdBy, String note) {
        String sql = """
                INSERT INTO share_links (seminar_id, token, expires_at, created_at, created_by, note)
                VALUES (?,?,?,?,?,?)
                """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            Instant now = Instant.now();
            ps.setLong(1, seminarId);
            ps.setString(2, token);
            ps.setObject(3, expiresAt);
            ps.setObject(4, now);
            ps.setLong(5, createdBy);
            ps.setString(6, note);
            ps.executeUpdate();
            ShareLink link = new ShareLink();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                link.setId(keys.getLong(1));
            }
            link.setSeminarId(seminarId);
            link.setToken(token);
            link.setExpiresAt(expiresAt);
            link.setCreatedAt(now);
            link.setCreatedBy(createdBy);
            link.setNote(note);
            return link;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ShareLink map(ResultSet rs) throws SQLException {
        ShareLink l = new ShareLink();
        l.setId(rs.getLong("id"));
        l.setSeminarId(rs.getLong("seminar_id"));
        l.setToken(rs.getString("token"));
        l.setExpiresAt(rs.getObject("expires_at", Instant.class));
        l.setCreatedAt(rs.getObject("created_at", Instant.class));
        long cb = rs.getLong("created_by");
        l.setCreatedBy(rs.wasNull() ? null : cb);
        l.setNote(rs.getString("note"));
        return l;
    }
}
