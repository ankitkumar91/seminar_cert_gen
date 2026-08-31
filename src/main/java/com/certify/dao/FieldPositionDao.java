package com.certify.dao;

import com.certify.db.Database;
import com.certify.model.FieldPosition;
import com.certify.model.FormField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FieldPositionDao {

    public List<FieldPosition> findBySeminar(long seminarId) {
        List<FieldPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM field_positions WHERE seminar_id = ?";
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

    public void ensureDefaults(long seminarId) {
        if (!findBySeminar(seminarId).isEmpty()) {
            return;
        }
        double[] ys = {46.5, 54.0, 59.0, 64.0, 80.5, 84.5};
        FormField[] fields = FormField.values();
        for (int i = 0; i < fields.length; i++) {
            upsert(FieldPosition.defaults(seminarId, fields[i], ys[i]));
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
