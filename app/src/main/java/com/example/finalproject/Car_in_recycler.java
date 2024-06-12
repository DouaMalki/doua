package com.example.finalproject;

public class Car_in_recycler {
    private int id;
    private String image_url;
    private double price;
    private double rating;
    private String model;
    private String make_date;

    public Car_in_recycler(int id, String image_url, double price, double rating, String model, String make_date) {
        this.id = id;
        this.image_url = image_url;
        this.price = price;
        this.rating = rating;
        this.model = model;
        this.make_date = make_date;
    }

    public int getId() { return id; }
    public String getImageUrl() { return image_url; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getModel() { return model; }
    public String getMake_date() { return make_date; }
}
