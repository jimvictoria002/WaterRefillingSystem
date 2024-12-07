package com.lu.waterrefilling;

public class WaterProduct {
    private int quantity = 1;
    private String price; // Changed to String to match Firestore data
    private String name;
    private String orderBy;
    private String id;
    private String imageUrl;

    public WaterProduct() {}

    public WaterProduct(String price, int quantity, String name, String orderBy) {
        this.price = price;
        this.quantity = quantity;
        this.name = name;
        this.orderBy = orderBy;
    }

    // Getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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

    @Override
    public String toString() {
        return "WaterProduct{" +
                "price='" + price + '\'' +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}
