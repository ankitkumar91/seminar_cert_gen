package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.FieldPosition;
import com.certify.model.FormField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldPositionDao {

    public List<FieldPosition> findBySeminar(long seminarId) {
        List<FieldPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM field_positions WHERE seminar_id = ? ORDER BY y_percent";
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

    public Set<String> keysForSeminar(long seminarId) {
        Set<String> keys = new HashSet<>();
        for (FieldPosition p : findBySeminar(seminarId)) {
            keys.add(p.getFieldKey());
        }
        return keys;
    }

    public void ensureDefaults(long seminarId) {
        if (!findBySeminar(seminarId).isEmpty()) {
            return;
        }
        upsert(FieldPosition.defaults(seminarId, FormField.FULL_NAME, 46.5));
    }

    public void addField(long seminarId, FormField field) {
        if (keysForSeminar(seminarId).contains(field.key())) {
            return;
        }
        int n = findBySeminar(seminarId).size();
        double y = Math.min(85, 46.5 + n * 6);
        upsert(FieldPosition.defaults(seminarId, field, y));
    }

    public void delete(long seminarId, String fieldKey) {
        String sql = "DELETE FROM field_positions WHERE seminar_id = ? AND field_key = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, seminarId);
            ps.setString(2, fieldKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void upsert(FieldPosition p) {
        String sql = """
                MERGE INTO field_positions (seminar_id, field_key, x_percent, y_percent, width_percent,
                  font_size, font_color, font_bold, text_align)
                KEY (seminar_id, field_key)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getSeminarId());
            ps.setString(2, p.getFieldKey());
            ps.setDouble(3, p.getXPercent());
            ps.setDouble(4, p.getYPercent());
            ps.setDouble(5, p.getWidthPercent());
            ps.setInt(6, p.getFontSize());
            ps.setString(7, p.getFontColor());
            ps.setBoolean(8, p.isFontBold());
            ps.setString(9, p.getTextAlign());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private FieldPosition map(ResultSet rs) throws SQLException {
        FieldPosition p = new FieldPosition();
        p.setId(rs.getLong("id"));
        p.setSeminarId(rs.getLong("seminar_id"));
        p.setFieldKey(rs.getString("field_key"));
        p.setXPercent(rs.getDouble("x_percent"));
        p.setYPercent(rs.getDouble("y_percent"));
        p.setWidthPercent(rs.getDouble("width_percent"));
        p.setFontSize(rs.getInt("font_size"));
        p.setFontColor(rs.getString("font_color"));
        p.setFontBold(rs.getBoolean("font_bold"));
        p.setTextAlign(rs.getString("text_align"));
        return p;
    }
}
