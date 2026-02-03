package service;

import db.DatabaseConnection;
import exception.OrderNotFoundException;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import repository.OrderItemRepository;
import repository.OrderRepository;
import service.delivery.DeliveryFactory;
import service.delivery.DeliveryOption;
import service.pricing.PricingService;
import util.Result;

import java.math.BigDecimal;
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

    public Result<Long> placeOrderV2(OrderRequest req) {

        if (req == null) {
            return Result.fail("Request is null");
        }

        if (req.getCustomerId() <= 0) {
            return Result.fail("Invalid customerId: " + req.getCustomerId());
        }

        if (req.getItems() == null || req.getItems().isEmpty()) {
            return Result.fail("Order must contain at least one item.");
        }

        boolean hasInvalidQty = req.getItems().stream().anyMatch(i -> i.getQuantity() <= 0);
        if (hasInvalidQty) {
            return Result.fail("Quantity must be > 0");
        }

        DeliveryOption option = DeliveryFactory.create(req.getOrderType());

        if (option.requiresAddress() && (req.getAddress() == null || req.getAddress().isBlank())) {
            return Result.fail("Address is required for DELIVERY");
        }

        PricingService pricingService = new PricingService();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                long orderId = orderRepository.create(conn, req.getCustomerId());

                BigDecimal itemsTotal = BigDecimal.ZERO;

                for (OrderRequestItem item : req.getItems()) {
                    MenuItem menuItem = menuService.getAvailableMenuItem(item.getMenuItemId());

                    orderItemRepository.addItem(
                            conn,
                            orderId,
                            menuItem.getId(),
                            item.getQuantity(),
                            menuItem.getPrice()
                    );

                    itemsTotal = itemsTotal.add(
                            menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
                }

                BigDecimal finalTotal = pricingService.applyFeesAndTax(itemsTotal, option);
                System.out.println("OrderType=" + option.code() + " finalTotal=" + finalTotal);

                paymentService.pay(orderId);

                conn.commit();
                return Result.ok(orderId);

            } catch (RuntimeException e) {
                conn.rollback();
                return Result.fail(e.getMessage());

            } catch (SQLException e) {
                conn.rollback();
                return Result.fail("SQL error while placing order");

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            return Result.fail("DB connection error");
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

    public record OrderDetails(Order order, List<OrderItem> items) {
    }
}
