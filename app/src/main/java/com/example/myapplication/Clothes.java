package com.example.myapplication;

import java.io.Serializable;

public class Clothes implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String image1;
    private String image2;
    private String image3;
    private double price;
    private String description;
    private int quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Clothes() {
    }

    public Clothes(String name, String image1, String image2, String image3, double price, String description, int quantity) {
        this.name = name;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }
}
