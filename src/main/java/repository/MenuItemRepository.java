package repository;

import db.DatabaseConnection;
import model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuItemRepository {

    public List<MenuItem> findAll() {
        String sql = "SELECT id, name, price, available, category FROM menu_items ORDER BY id";

        List<MenuItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load menu items", e);
        }
    }

    public Optional<MenuItem> findById(long id) {
        String sql = "SELECT id, name, price, available, category FROM menu_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find menu item by id=" + id, e);
        }
    }

    public long create(MenuItem item) {
        String sql = "INSERT INTO menu_items(name, price, available, category) VALUES(?,?,?,?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getPrice() == null ? BigDecimal.ZERO : item.getPrice());
            ps.setBoolean(3, item.isAvailable());
            ps.setString(4, item.getCategory());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create menu item", e);
        }
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        BigDecimal price = rs.getBigDecimal("price");
        boolean available = rs.getBoolean("available");
        String category = rs.getString("category");
        return new MenuItem(id, name, price, available, category);
    }
}
