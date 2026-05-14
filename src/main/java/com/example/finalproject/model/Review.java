package com.example.finalproject.model;

public class Review {

    private int id;
    private String username;
    private String comment;
    private int rating;
    private int productId;

    public Review() {}

    public Review(int id, String username, String comment, int rating, int productId) {
        this.id = id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.productId = productId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
}