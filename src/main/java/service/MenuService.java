package service;

import exception.MenuItemNotAvailableException;
import model.MenuItem;
import repository.MenuItemRepository;

public class MenuService {

    private final MenuItemRepository menuRepo;

    public MenuService(MenuItemRepository menuRepo) {
        this.menuRepo = menuRepo;
    }

    public MenuItem getAvailableMenuItem(long menuItemId) {
        MenuItem item = menuRepo.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotAvailableException("Menu item not found: id=" + menuItemId));

        if (!item.isAvailable()) {
            throw new MenuItemNotAvailableException("Menu item is not available: id=" + menuItemId);
        }
        return item;
    }
}

