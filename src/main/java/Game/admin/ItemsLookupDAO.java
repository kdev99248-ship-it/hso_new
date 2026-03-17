package Game.admin;

import Game.core.SQL;
import java.sql.*;
import java.util.*;

public class ItemsLookupDAO {

    public static class ItemRow {
        public final int id;
        public final String name;
        public ItemRow(int id, String name) { this.id = id; this.name = name; }
    }

    private static List<ItemRow> queryItems(Connection c, String table, String keyword, int limit) throws SQLException {
        String sql = "SELECT id, name FROM " + table + " WHERE name LIKE ? ORDER BY id ASC LIMIT ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            String pattern = (keyword == null || keyword.isBlank()) ? "%" : "%" + keyword.trim() + "%";
            ps.setString(1, pattern);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<ItemRow> list = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    list.add(new ItemRow(id, name == null ? "" : name));
                }
                return list;
            }
        }
    }

    public List<ItemRow> listItem4(String keyword, int limit) throws SQLException {
        try (Connection c = SQL.gI().getConnection()) {
            return queryItems(c, "item4", keyword, Math.max(1, limit));
        }
    }

    public List<ItemRow> listItem7(String keyword, int limit) throws SQLException {
        try (Connection c = SQL.gI().getConnection()) {
            return queryItems(c, "item7", keyword, Math.max(1, limit));
        }
    }
}
