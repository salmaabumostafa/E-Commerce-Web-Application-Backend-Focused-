package com.example.finalproject.controller;

import com.example.finalproject.helper.ReviewDb;
import com.example.finalproject.model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.ProductDb;
import com.example.finalproject.helper.UserDb;
import com.example.finalproject.model.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = (String) request.getAttribute("user");

        // Role
        String role = "user";
        try {
            role = UserDb.getUserRole(user);
        } catch (Exception e) {
            System.out.println("[ProductsServlet] Role error: " + e.getMessage());
        }

        // Products
        List<Product> products = null;
        String error = null;
        try {
            products = ProductDb.getProductList(user);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Too many requests")) {
                error = "Too many requests. Please wait and try again.";
            } else {
                error = "Could not load products. Please try again.";
            }
        } catch (Exception e) {
            error = "Server error. Please try again.";
        }

        // All reviews of all products
        List<Review> reviews = new ArrayList<>();
        if (products != null) {
            for (Product p : products) {
                try {
                    reviews.addAll(ReviewDb.getReviewsByProductId(p.getId()));
                } catch (Exception e) {
                    System.out.println("[ProductsServlet] Reviews error: " + e.getMessage());
                }
            }
        }

        request.setAttribute("products", products);
        request.setAttribute("reviews", reviews);
        request.setAttribute("error", error);
        request.setAttribute("role", role);
        request.setAttribute("user", user);

        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}