package repository;

import model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepository {

    public void addItem(Connection conn, long orderId, long menuItemId, int quantity, BigDecimal priceAtOrder) throws SQLException {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity, price_at_order) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, menuItemId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, priceAtOrder);
            ps.executeUpdate();
        }
    }

    public List<OrderItem> findByOrderId(Connection conn, long orderId) throws SQLException {
        String sql = "SELECT id, order_id, menu_item_id, quantity, price_at_order FROM order_items WHERE order_id = ? ORDER BY id";
        List<OrderItem> items = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapRow(rs));
            }
        }
        return items;
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long orderId = rs.getLong("order_id");
        Long menuItemId = rs.getLong("menu_item_id");
        int quantity = rs.getInt("quantity");
        BigDecimal price = rs.getBigDecimal("price_at_order");
        return new OrderItem(id, orderId, menuItemId, quantity, price);
    }
}
