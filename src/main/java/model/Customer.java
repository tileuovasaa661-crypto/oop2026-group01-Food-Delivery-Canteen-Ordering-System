package model;

import java.time.LocalDateTime;

public class Customer extends BaseEntity {

    private String name;
    private String phone;
    private LocalDateTime createdAt;

    public Customer(Long id, String name, String phone) {
        super(id);
        this.name = name;
        this.phone = phone;
    }

    public Customer(Long id, String name, String phone, LocalDateTime createdAt) {
        super(id);
        this.name = name;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void displayInfo() {
        System.out.println("Customer: " + name + ", Phone: " + phone);
    }
}