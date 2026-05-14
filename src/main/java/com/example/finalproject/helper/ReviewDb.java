package com.example.finalproject.helper;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.example.finalproject.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDb {

    private static final String URI = "mongodb+srv://root:root@cluster0.wftrant.mongodb.net/?appName=Cluster0";
    private static final String DB_NAME = "ecommerce";
    private static final String COLLECTION_NAME = "reviews";


    //Create a connection to MongoDB and return the collection
    private static MongoCollection<Document> getCollection() {
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(URI))
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            return database.getCollection(COLLECTION_NAME);

        } catch (Exception e) {
            System.out.println("[ReviewDb] getCollection ERROR: " + e.getMessage());
            return null;
        }
    }

    //Get all reviews for a specific product
    public static List<Review> getReviewsByProductId(int productId) {

        List<Review> reviews = new ArrayList<>();

        try {
            MongoCollection<Document> collection = getCollection();

            if (collection == null) {
                System.out.println("[ReviewDb] getReviewsByProductId: collection unavailable for productId=" + productId);
                return reviews;
            }

            for (Document doc : collection.find(new Document("productId", productId))) {
                reviews.add(new Review(
                        0,
                        doc.getString("username"),
                        doc.getString("comment"),
                        doc.getInteger("rating"),
                        doc.getInteger("productId")
                ));
            }

            System.out.println("[ReviewDb] getReviewsByProductId: found " + reviews.size() + " reviews for productId=" + productId);

        } catch (Exception e) {
            System.out.println("[ReviewDb] getReviewsByProductId ERROR for productId=" + productId + ": " + e.getMessage());
        }

        return reviews;
    }

    //Add a new review for a product
    public static void addReview(int productId, String username, String comment, int rating) {

        try {
            MongoCollection<Document> collection = getCollection();

            if (collection == null) {
                System.out.println("[ReviewDb] addReview: collection unavailable, review not saved");
                throw new Exception("Database connection failed. Review not saved.");
            }

            Document doc = new Document()
                    .append("productId", productId)
                    .append("username", username)
                    .append("comment", comment)
                    .append("rating", rating);

            collection.insertOne(doc);
            System.out.println("[ReviewDb] addReview: review added by '" + username + "' for productId=" + productId);

        } catch (Exception e) {
            System.out.println("[ReviewDb] addReview ERROR: " + e.getMessage());
            throw new RuntimeException("Could not save review. Please try again.");
        }
    }
}