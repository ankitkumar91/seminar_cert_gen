package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.Seminar;
import com.certify.model.SeminarStatus;
import com.certify.util.Paging;

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

    private static final String LIST_COLUMNS = """
            s.id, s.title, s.venue, s.seminar_date, s.organizer, s.status,
            s.template_relpath, s.template_width, s.template_height,
            s.created_by, s.created_at, s.updated_at, s.approved_by, s.approved_at
            """;

    public List<Seminar> findAll() {
        return query("SELECT * FROM seminars ORDER BY created_at DESC");
    }

    public List<Seminar> findPendingApproval() {
        return query("SELECT * FROM seminars WHERE status = 'PENDING_APPROVAL' ORDER BY updated_at DESC");
    }

    public List<Seminar> findForDeveloper() {
        return query("SELECT * FROM seminars WHERE template_relpath IS NOT NULL ORDER BY status ASC, updated_at DESC");
    }

    public int countAll() {
        return scalar("SELECT COUNT(*) FROM seminars");
    }

    public int countByStatus(SeminarStatus status) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM seminars WHERE status = ?")) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countAdmin(SeminarListQuery filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM seminars s WHERE 1=1");
        appendFilters(sql, filter);
        return scalarFiltered(sql.toString(), filter);
    }

    public List<Seminar> findAdminPage(SeminarListQuery filter, int page) {
        StringBuilder sql = new StringBuilder("""
                SELECT %s,
                  (SELECT COUNT(*) FROM share_links sl WHERE sl.seminar_id = s.id) AS link_count,
                  (SELECT COUNT(*) FROM submissions sub WHERE sub.seminar_id = s.id) AS download_count
                FROM seminars s
                WHERE 1=1
                """.formatted(LIST_COLUMNS));
        appendFilters(sql, filter);
        sql.append(" ORDER BY s.created_at DESC OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");
        return listFiltered(sql.toString(), filter, page, true);
    }

    public int countDeveloper(SeminarListQuery filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM seminars s WHERE s.template_relpath IS NOT NULL");
        appendFilters(sql, filter);
        return scalarFiltered(sql.toString(), filter);
    }

    public List<Seminar> findDeveloperPage(SeminarListQuery filter, int page) {
        StringBuilder sql = new StringBuilder("""
                SELECT %s
                FROM seminars s
                WHERE s.template_relpath IS NOT NULL
                """.formatted(LIST_COLUMNS));
        appendFilters(sql, filter);
        sql.append("""
                 ORDER BY CASE s.status
                   WHEN 'PENDING_APPROVAL' THEN 0
                   WHEN 'DRAFT' THEN 1
                   ELSE 2
                 END, s.updated_at DESC
                 OFFSET ? ROWS FETCH FIRST ? ROWS ONLY
                """);
        return listFiltered(sql.toString(), filter, page, false);
    }

    public Seminar find(long id) {
        String sql = "SELECT * FROM seminars WHERE id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs, false) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private static void appendFilters(StringBuilder sql, SeminarListQuery filter) {
        if (filter.hasText()) {
            sql.append(" AND (LOWER(s.title) LIKE ? ESCAPE '\\' OR LOWER(s.organizer) LIKE ? ESCAPE '\\')");
        }
        if (filter.getStatus() != null) {
            sql.append(" AND s.status = ?");
        }
    }

    private int bindFilters(PreparedStatement ps, SeminarListQuery filter, int start)
            throws SQLException {
        int i = start;
        if (filter.hasText()) {
            String like = filter.likePattern();
            ps.setString(i++, like);
            ps.setString(i++, like);
        }
        if (filter.getStatus() != null) {
            ps.setString(i++, filter.getStatus().name());
        }
        return i;
    }

    private int scalarFiltered(String sql, SeminarListQuery filter) {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindFilters(ps, filter, 1);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Seminar> listFiltered(String sql, SeminarListQuery filter, int page,
                                       boolean withCounts) {
        List<Seminar> list = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bindFilters(ps, filter, 1);
            ps.setInt(i++, Paging.offset(page));
            ps.setInt(i, Paging.PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs, withCounts));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private int scalar(String sql) {
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
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
                list.add(map(rs, false));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Seminar map(ResultSet rs, boolean withCounts) throws SQLException {
        Seminar s = new Seminar();
        s.setId(rs.getLong("id"));
        s.setTitle(rs.getString("title"));
        try {
            s.setDescription(rs.getString("description"));
        } catch (SQLException ignored) {
            // List queries omit the CLOB.
        }
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
        if (withCounts) {
            s.setLinkCount(rs.getInt("link_count"));
            s.setDownloadCount(rs.getInt("download_count"));
        }
        return s;
    }
}
