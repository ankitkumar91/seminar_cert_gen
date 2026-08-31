package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeminarDao {

    public List<Seminar> findAll() {
        return query("SELECT * FROM seminars ORDER BY created_at DESC");
    }

    public List<Seminar> findPendingApproval() {
        return query("SELECT * FROM seminars WHERE status = 'PENDING_APPROVAL' ORDER BY updated_at DESC");
    }

    public List<Seminar> findForDeveloper() {
        return query("SELECT * FROM seminars WHERE template_relpath IS NOT NULL ORDER BY status ASC, updated_at DESC");
    }

    public Seminar find(long id) {
        List<Seminar> list = query("SELECT * FROM seminars WHERE id = " + id);
        return list.isEmpty() ? null : list.get(0);
    }

    public long insert(Seminar s) {
        String sql = """
                INSERT INTO seminars (title, description, venue, seminar_date, organizer, status,
                  created_by, created_at, updated_at)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCore(ps, s);
            ps.setString(6, s.getStatus().name());
            ps.setLong(7, s.getCreatedBy());
            ps.setObject(8, s.getCreatedAt());
            ps.setObject(9, s.getUpdatedAt());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDetails(Seminar s) {
        String sql = """
                UPDATE seminars SET title=?, description=?, venue=?, seminar_date=?, organizer=?, updated_at=?
                WHERE id=?
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindCore(ps, s);
            ps.setObject(6, Instant.now());
            ps.setLong(7, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void attachTemplate(long id, String relpath, int width, int height) {
        String sql = """
                UPDATE seminars SET template_relpath=?, template_width=?, template_height=?,
                  status='PENDING_APPROVAL', approved_by=NULL, approved_at=NULL, updated_at=?
                WHERE id=?
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, relpath);
            ps.setInt(2, width);
            ps.setInt(3, height);
            ps.setObject(4, Instant.now());
            ps.setLong(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void approve(long seminarId, long developerId) {
        String sql = "UPDATE seminars SET status='APPROVED', approved_by=?, approved_at=?, updated_at=? WHERE id=?";
        Instant now = Instant.now();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, developerId);
            ps.setObject(2, now);
            ps.setObject(3, now);
            ps.setLong(4, seminarId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void bindCore(PreparedStatement ps, Seminar s) throws SQLException {
        ps.setString(1, s.getTitle());
        ps.setString(2, s.getDescription());
        ps.setString(3, s.getVenue());
        ps.setObject(4, s.getSeminarDate());
        ps.setString(5, s.getOrganizer());
    }

    private List<Seminar> query(String sql) {
        List<Seminar> list = new ArrayList<>();
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

    private Seminar map(ResultSet rs) throws SQLException {
        Seminar s = new Seminar();
        s.setId(rs.getLong("id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setVenue(rs.getString("venue"));
        s.setSeminarDate(rs.getObject("seminar_date", LocalDate.class));
        s.setOrganizer(rs.getString("organizer"));
        s.setStatus(SeminarStatus.valueOf(rs.getString("status")));
        s.setTemplateRelpath(rs.getString("template_relpath"));
        int w = rs.getInt("template_width");
        s.setTemplateWidth(rs.wasNull() ? null : w);
        int h = rs.getInt("template_height");
        s.setTemplateHeight(rs.wasNull() ? null : h);
        s.setCreatedBy(rs.getLong("created_by"));
        s.setCreatedAt(rs.getObject("created_at", Instant.class));
        s.setUpdatedAt(rs.getObject("updated_at", Instant.class));
        long ab = rs.getLong("approved_by");
        s.setApprovedBy(rs.wasNull() ? null : ab);
        s.setApprovedAt(rs.getObject("approved_at", Instant.class));
        return s;
    }
}
