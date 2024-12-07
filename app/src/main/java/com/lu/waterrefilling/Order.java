package com.lu.waterrefilling;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private String address;
    private String orderBy, userId, orderId, status;
    private long orderAt;
    private String price;
    private String productName;
    private int quantity;
    private int total;


    private Boolean isPaid;

    // Default constructor
    public Order() {}

    // Parameterized constructor
    public Order(String address,
                 String orderBy,
                 long orderAt,
                 String price,
                 String productName,
                 int quantity,
                 int total,
                 Boolean isPaid,
                 String userId,
                 String orderId,
                 String status) {
        this.address = address;
        this.orderBy = orderBy;
        this.orderAt = orderAt;
        this.price = price;
        this.productName = productName;
        this.quantity = quantity;
        this.total = total;
        this.isPaid = isPaid;
        this.userId = userId;
        this.orderId = orderId;
        this.status = status;
    }

    // Getters and setters


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public long getOrderAt() {
        return orderAt;
    }

    public void setOrderAt(long orderAt) {
        this.orderAt = orderAt;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String orderAtFormatted(){
        Date date = new Date(this.orderAt);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy h:mm a");
        return formatter.format(date);
    }

    @Override
    public String toString() {
        return "Order{" +
                "address='" + address + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", status='" + status + '\'' +
                ", orderAt=" + orderAt +
                ", isPaid=" + isPaid +
                ", price='" + price + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}
