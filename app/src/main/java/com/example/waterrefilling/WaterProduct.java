package com.example.waterrefilling;

public class WaterProduct {
    private int  quantity = 1, imageResourceId; // Add imageResourceId to store drawable resource ID
    private double price;
    private String name, orderBy, id;

    public WaterProduct() {}

    public WaterProduct(int price, int quantity, String name, String orderBy, int imageResourceId) {
        this.price = price;
        this.quantity = quantity;
        this.name = name;
        this.orderBy = orderBy;
        this.imageResourceId = imageResourceId; // Initialize the image resource ID
    }

    // Getters and setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    @Override
    public String toString() {
        return "WaterProduct{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", imageResourceId=" + imageResourceId +
                ", name='" + name + '\'' +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}
