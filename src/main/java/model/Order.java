package model;

import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long customerId;
    private String status;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(Long id, Long customerId, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
