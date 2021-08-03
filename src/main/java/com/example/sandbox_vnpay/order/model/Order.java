package com.example.sandbox_vnpay.order.model;

import com.example.sandbox_vnpay.order.util.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(name = "orders")
@Table(name = "orders")
public class Order {

    @Id
    @SequenceGenerator(
            name = "order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "amount",
            nullable = false
    )
    private Long amount;

    @Column(
            name = "description"
    )
    private String description;

    @Column(
            name = "order_date",
            nullable = false
    )
    private LocalDateTime orderDate;

    @Column(
            name = "order_status_code",
            nullable = false
    )
    private Status orderStatus = Status.PENDING;

    @Column(
            name = "ip_address"
//            TODO: Change this to not null
//            nullable = false
    )
    private String ipAddress;

    public Order() {
    }

    public Order(Status orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Order(Long amount,
                 String description) {
        this.amount = amount;
        this.description = description;
    }

    public Order(Long id,
                 Long amount,
                 String description,
                 LocalDateTime orderDate,
                 Status  statusCode,
                 String ipAddress) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.orderDate = orderDate;
        this.orderStatus = statusCode;
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return orderDate.format(formatter);
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus.name();
    }

    public void setOrderStatus(Status orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", amount=" + amount +
                ", description=" + description +
                ", orderDate=" + orderDate +
                ", orderStatus=" + orderStatus +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
