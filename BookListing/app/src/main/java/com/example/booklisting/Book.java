package com.example.booklisting;

public class Book {
    private String title, image, buyBook;
    private double price;

    public Book(String title, double price, String image, String buyBook) {
        this.title = title;
        this.price = price;
        this.image = image;
        this.buyBook = buyBook;
    }

    public String getTitle() {
        return title;
    }

    public String getBuyBook() {
        return buyBook;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
