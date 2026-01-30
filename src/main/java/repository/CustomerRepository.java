package repository;

import db.DatabaseConnection;
import model.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class CustomerRepository {

    public Optional<Customer> findById(long id) {
        String sql = "SELECT id, name, phone, created_at FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find customer by id=" + id, e);
        }
    }

    public long create(Customer c) {
        String sql = "INSERT INTO customers(name, phone, created_at) VALUES(?, ?, now()) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create customer", e);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts == null ? null : ts.toLocalDateTime();
        return new Customer(id, name, phone, createdAt);
    }
}
