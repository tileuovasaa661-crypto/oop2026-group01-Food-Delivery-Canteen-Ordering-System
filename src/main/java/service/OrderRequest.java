package service;

import model.OrderType;
import java.util.List;

public class OrderRequest {
    private final long customerId;
    private final OrderType orderType;
    private final String address;
    private final List<OrderRequestItem> items;

    public OrderRequest(long customerId, OrderType orderType, String address, List<OrderRequestItem> items) {
        this.customerId = customerId;
        this.orderType = orderType;
        this.address = address;
        this.items = items;
    }

    public long getCustomerId() { return customerId; }
    public OrderType getOrderType() { return orderType; }
    public String getAddress() { return address; }
    public List<OrderRequestItem> getItems() { return items; }
}
