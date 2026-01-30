package model;

import java.math.BigDecimal;

public class MenuItem {
    private Long id;
    private String name;
    private BigDecimal price;
    private boolean available;
    private String category;

    public MenuItem() {}

    public MenuItem(Long id, String name, BigDecimal price, boolean available, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", available=" + available +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        MenuItem menuItem = (MenuItem) o;
        return id != null && id.equals(menuItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
