package service;

public class OrderRequestItem {
    private final long menuItemId;
    private final int quantity;

    public OrderRequestItem(long menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public long getMenuItemId() { return menuItemId; }
    public int getQuantity() { return quantity; }
}
