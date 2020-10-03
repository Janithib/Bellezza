package com.example.janithi;

import java.io.Serializable;

public class Product implements Serializable {
    int image;
    String name, manufacturer, origin;
    double price;

    public Product(int image, String name, String manufacturer, String origin, double price) {
        this.image = image;
        this.name = name;
        this.manufacturer = manufacturer;
        this.origin = origin;
        this.price = price;
    }

    public Product() {
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getOrigin() {
        return origin;
    }

    public double getPrice() {
        return price;
    }
}
