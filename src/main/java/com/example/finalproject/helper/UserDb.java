package com.example.finalproject.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDb {

    private static final String URL = "jdbc:mysql://localhost:3306/products?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // DB Connection
    private static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    // Validate login data
    // return true if username and password are correct

    public static boolean validateUser(String username, String password) throws Exception {

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            System.out.println("[UserDb] validateUser: empty credentials provided");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ResultSet rs = ps.executeQuery();

            boolean found = rs.next();
            System.out.println("[UserDb] validateUser: " + username + " → " + (found ? "SUCCESS" : "FAILED"));
            return found;

        } catch (Exception e) {
            System.out.println("[UserDb] validateUser ERROR for '" + username + "': " + e.getMessage());
            throw new Exception("Login check failed. Please try again.");
        }
    }


     //Register a new user
     //return true if it works successfully, false if username already exists
    public static boolean registerUser(String username, String password) throws Exception {

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            System.out.println("[UserDb] registerUser: empty fields provided");
            throw new Exception("Username and password cannot be empty.");
        }

        // Check that username does not exist

        try (Connection conn = getConnection();
             PreparedStatement check = conn.prepareStatement(
                     "SELECT id FROM users WHERE username = ?")) {

            check.setString(1, username.trim());
            if (check.executeQuery().next()) {
                System.out.println("[UserDb] registerUser: username already exists → " + username);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[UserDb] registerUser CHECK ERROR: " + e.getMessage());
            throw new Exception("Could not verify username availability.");
        }

        // Insert the new user
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (username, password) VALUES (?, ?)")) {

            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ps.executeUpdate();

            System.out.println("[UserDb] registerUser: new user created → " + username);
            return true;

        } catch (Exception e) {
            System.out.println("[UserDb] registerUser INSERT ERROR: " + e.getMessage());
            throw new Exception("Registration failed. Please try again.");
        }
    }

    // Fetch the user's role

    public static String getUserRole(String username) throws Exception {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT role FROM users WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                System.out.println("[UserDb] getUserRole: " + username + " → " + role);
                return role;
            }

            System.out.println("[UserDb] getUserRole: user not found, returning default 'user'");
            return "user"; // default role

        } catch (Exception e) {
            System.out.println("[UserDb] getUserRole ERROR for '" + username + "': " + e.getMessage());
            throw new Exception("Could not retrieve user role.");
        }
    }

    // Delete User
    public static void deleteUser(String username) throws Exception {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM users WHERE username = ?")) {

            ps.setString(1, username);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                System.out.println("[UserDb] deleteUser: user deleted → " + username);
            } else {
                System.out.println("[UserDb] deleteUser: user not found → " + username);
                throw new Exception("User not found.");
            }

        } catch (Exception e) {
            System.out.println("[UserDb] deleteUser ERROR for '" + username + "': " + e.getMessage());
            throw new Exception("Could not delete account. Please try again.");
        }
    }
}