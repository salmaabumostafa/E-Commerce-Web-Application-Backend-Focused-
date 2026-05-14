package com.example.finalproject.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.finalproject.model.Product;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDb {

    private static final String URL = "jdbc:mysql://localhost:3306/products?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static List<Product> getProductList(String user) throws Exception {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            // Rate Limiting
            String rateKey = "rate:" + user;
            String count = jedis.get(rateKey);
            if (count != null && Integer.parseInt(count) >= 5) {
                throw new RuntimeException("Too many requests");
            }
            jedis.incr(rateKey);
            jedis.expire(rateKey, 10);

            // Redis Cache
            String cached = jedis.get("products");
            if (cached != null) {
                System.out.println(">>> FROM REDIS CACHE");
                Type type = new TypeToken<ArrayList<Product>>() {}.getType();
                return new Gson().fromJson(cached, type);
            }

            // DB
            List<Product> products = new ArrayList<>();
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM product_cards")) {

                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("item"),
                            rs.getFloat("price")
                    ));
                }
            }

            // Save to Cache
            jedis.setex("products", 60, new Gson().toJson(products));

            return products;
        }
    }

    public static Product getProductById(int id) throws Exception {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM product_cards WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("item"),
                        rs.getFloat("price")
                );
            }
        }
        return null;
    }

    public static void addProduct(String name, float price) throws Exception {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO product_cards (item, price) VALUES (?, ?)")) {

            ps.setString(1, name);
            ps.setFloat(2, price);
            ps.executeUpdate();
        }

        // Clear the cache to update it
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del("products");
        }

        System.out.println("[ProductDb] Product added: " + name);
    }

    public static void deleteProduct(int id) throws Exception {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM product_cards WHERE id = ?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }

        // Clear cache
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del("products");
        }

        System.out.println("[ProductDb] Product deleted: id=" + id);
    }
}