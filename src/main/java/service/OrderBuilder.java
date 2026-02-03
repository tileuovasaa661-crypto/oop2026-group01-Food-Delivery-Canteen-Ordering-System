package service;

import model.OrderType;

import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private long customerId;
    private OrderType orderType = OrderType.PICKUP;
    private String address;
    private final List<OrderRequestItem> items = new ArrayList<>();

    public OrderBuilder customer(long customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderBuilder type(OrderType type) {
        this.orderType = type;
        return this;
    }

    public OrderBuilder address(String address) {
        this.address = address;
        return this;
    }

    public OrderBuilder addItem(long menuItemId, int qty) {
        this.items.add(new OrderRequestItem(menuItemId, qty));
        return this;
    }

    public OrderRequest build() {
        return new OrderRequest(customerId, orderType, address, items);
    }
}
