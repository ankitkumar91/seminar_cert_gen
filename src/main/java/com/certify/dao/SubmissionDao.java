package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.Submission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDao {

    public long insert(Submission s) {
        String sql = """
                INSERT INTO submissions (seminar_id, share_link_id, full_name, email, phone, college,
                  enrollment_no, designation, created_at, ip_address)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, s.getSeminarId());
            if (s.getShareLinkId() == null) {
                ps.setObject(2, null);
            } else {
                ps.setLong(2, s.getShareLinkId());
            }
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getCollege());
            ps.setString(7, s.getEnrollmentNo());
            ps.setString(8, s.getDesignation());
            ps.setObject(9, Instant.now());
            ps.setString(10, s.getIpAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countBySeminar(long seminarId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM submissions WHERE seminar_id = ?")) {
            ps.setLong(1, seminarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Submission> recentBySeminar(long seminarId, int limit) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE seminar_id = ? ORDER BY created_at DESC FETCH FIRST ? ROWS ONLY";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, seminarId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Submission s = new Submission();
                    s.setId(rs.getLong("id"));
                    s.setFullName(rs.getString("full_name"));
                    s.setEmail(rs.getString("email"));
                    s.setCollege(rs.getString("college"));
                    s.setCreatedAt(rs.getObject("created_at", Instant.class));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
