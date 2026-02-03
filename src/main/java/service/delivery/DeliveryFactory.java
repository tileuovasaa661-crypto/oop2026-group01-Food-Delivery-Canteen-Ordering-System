package service.delivery;

import model.OrderType;

public class DeliveryFactory {
    public static DeliveryOption create(OrderType type) {
        return switch (type) {
            case PICKUP -> new PickupOption();
            case DELIVERY -> new DeliveryOptionImpl();
            case DINE_IN -> new DineInOption();
        };
    }
}
