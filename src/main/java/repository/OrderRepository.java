package repository;

import model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    public long create(Connection conn, long customerId) throws SQLException {
        String sql = "INSERT INTO orders(customer_id, status, created_at) VALUES(?, 'ACTIVE', now()) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public Optional<Order> findById(Connection conn, long orderId) throws SQLException {
        String sql = "SELECT id, customer_id, status, created_at FROM orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    public List<Order> findActiveOrders(Connection conn) throws SQLException {
        String sql = "SELECT id, customer_id, status, created_at FROM orders WHERE status = 'ACTIVE' ORDER BY id";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) orders.add(mapRow(rs));
        }
        return orders;
    }

    public boolean markCompleted(Connection conn, long orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'COMPLETED' WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long customerId = rs.getLong("customer_id");
        String status = rs.getString("status");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts == null ? null : ts.toLocalDateTime();
        return new Order(id, customerId, status, createdAt);
    }
}
