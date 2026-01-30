package service;

import db.DatabaseConnection;
import exception.InvalidQuantityException;
import exception.OrderNotFoundException;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import repository.OrderItemRepository;
import repository.OrderRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuService menuService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MenuService menuService,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuService = menuService;
        this.paymentService = paymentService;
    }

    public long placeOrder(long customerId, List<OrderRequestItem> items) {

        if (customerId <= 0) {
            throw new IllegalArgumentException("Invalid customerId: " + customerId);
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidQuantityException("Order must contain at least one item.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                long orderId = orderRepository.create(conn, customerId);

                for (OrderRequestItem req : items) {
                    if (req.getQuantity() <= 0) {
                        throw new InvalidQuantityException(
                                "Invalid quantity for menuItemId=" + req.getMenuItemId() + ": " + req.getQuantity()
                        );
                    }

                    MenuItem menuItem = menuService.getAvailableMenuItem(req.getMenuItemId());

                    orderItemRepository.addItem(
                            conn,
                            orderId,
                            menuItem.getId(),     // Long -> long (auto-unboxing)
                            req.getQuantity(),
                            menuItem.getPrice()
                    );
                }

                paymentService.pay(orderId);

                conn.commit();
                return orderId;

            } catch (RuntimeException e) {
                conn.rollback();
                throw e;

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("SQL error while placing order", e);

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB connection error", e);
        }
    }

    public List<OrderDetails> viewActiveOrders() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Order> activeOrders = orderRepository.findActiveOrders(conn);

            List<OrderDetails> result = new ArrayList<>();
            for (Order o : activeOrders) {
                List<OrderItem> items = orderItemRepository.findByOrderId(conn, o.getId());
                result.add(new OrderDetails(o, items));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load active orders", e);
        }
    }

    public void markOrderCompleted(long orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid orderId: " + orderId);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                boolean updated = orderRepository.markCompleted(conn, orderId);
                if (!updated) {
                    throw new OrderNotFoundException("Order not found: id=" + orderId);
                }

                conn.commit();

            } catch (RuntimeException e) {
                conn.rollback();
                throw e;

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("SQL error while completing order", e);

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB connection error", e);
        }
    }

    public static class OrderDetails {
        private final Order order;
        private final List<OrderItem> items;

        public OrderDetails(Order order, List<OrderItem> items) {
            this.order = order;
            this.items = items;
        }

        public Order getOrder() { return order; }
        public List<OrderItem> getItems() { return items; }
    }
}
