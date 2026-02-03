package db;

import exception.InvalidQuantityException;
import exception.MenuItemNotAvailableException;
import exception.OrderNotFoundException;
import model.MenuItem;
import model.OrderType;
import repository.MenuItemRepository;
import repository.OrderItemRepository;
import repository.OrderRepository;
import service.MenuService;
import service.OrderBuilder;
import service.OrderService;
import service.PaymentService;
import util.Result;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final Scanner sc = new Scanner(System.in);

    private final MenuItemRepository menuItemRepository = new MenuItemRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    private final MenuService menuService = new MenuService(menuItemRepository);
    private final PaymentService paymentService = new PaymentService();
    private final OrderService orderService = new OrderService(
            orderRepository,
            orderItemRepository,
            menuService,
            paymentService
    );

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("=== Canteen Ordering System ===");

        while (true) {
            printMenu();
            int choice = readInt("Choose option: ");

            try {
                switch (choice) {
                    case 1 -> placeOrderFlow();
                    case 2 -> viewActiveOrdersFlow();
                    case 3 -> completeOrderFlow();
                    case 4 -> showMenuItemsFlow();
                    case 0 -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            } catch (MenuItemNotAvailableException | InvalidQuantityException | OrderNotFoundException e) {
                System.out.println("x " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("""
                -------------------------
                1) Place an order
                2) View active orders
                3) Mark order as completed
                4) Show menu items
                0) Exit
                -------------------------
                """);
    }

    private void showMenuItemsFlow() {
        List<MenuItem> items = menuItemRepository.findAll();
        if (items.isEmpty()) {
            System.out.println("Menu is empty.");
            return;
        }

        System.out.println("=== MENU ===");
        for (MenuItem item : items) {
            System.out.printf("id=%d | %s | price=%s | available=%s | category=%s%n",
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.isAvailable() ? "YES" : "NO",
                    item.getCategory()
            );
        }
    }

    private void placeOrderFlow() {
        System.out.println("=== PLACE ORDER ===");

        long customerId = readLong("Enter customerId: ");

        System.out.println("Choose order type: 1=PICKUP, 2=DELIVERY, 3=DINE_IN");
        int t = readInt("type: ");

        OrderType type = switch (t) {
            case 2 -> OrderType.DELIVERY;
            case 3 -> OrderType.DINE_IN;
            default -> OrderType.PICKUP;
        };

        String address = null;
        if (type == OrderType.DELIVERY) {
            System.out.print("Enter delivery address: ");
            address = sc.nextLine().trim();
        }

        System.out.println("Add items to order.");
        System.out.println("Tip: use option 4 (Show menu items) to see menuItem ids.");

        OrderBuilder builder = new OrderBuilder()
                .customer(customerId)
                .type(type)
                .address(address);

        while (true) {
            long menuItemId = readLong("menuItemId (0 to finish): ");
            if (menuItemId == 0) break;

            int quantity = readInt("quantity: ");
            builder.addItem(menuItemId, quantity);
        }
        
        Result<Long> result = orderService.placeOrderV2(builder.build());

        if (result.isOk()) {
            System.out.println("Order placed successfully. orderId=" + result.getData());
        } else {
            System.out.println("x " + result.getError());
        }
    }

    private void viewActiveOrdersFlow() {
        System.out.println("=== ACTIVE ORDERS ===");

        List<OrderService.OrderDetails> active = orderService.viewActiveOrders();
        if (active.isEmpty()) {
            System.out.println("No active orders.");
            return;
        }

        for (OrderService.OrderDetails od : active) {
            var o = od.order();

            System.out.printf("Order id=%d | customerId=%d | status=%s | createdAt=%s%n",
                    o.getId(),
                    o.getCustomerId(),
                    o.getStatus(),
                    o.getCreatedAt()
            );

            if (od.items().isEmpty()) {
                System.out.println("(no items)");
            } else {
                System.out.println("Items:");
                for (var it : od.items()) {
                    System.out.printf("   - orderItemId=%d | menuItemId=%d | qty=%d | priceAtOrder=%s%n",
                            it.getId(),
                            it.getMenuItemId(),
                            it.getQuantity(),
                            it.getPrice()
                    );
                }
            }

            System.out.println();
        }
    }

    private void completeOrderFlow() {
        System.out.println("=== COMPLETE ORDER ===");
        long orderId = readLong("Enter orderId to complete: ");
        orderService.markOrderCompleted(orderId);
        System.out.println("Order completed. orderId=" + orderId);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
