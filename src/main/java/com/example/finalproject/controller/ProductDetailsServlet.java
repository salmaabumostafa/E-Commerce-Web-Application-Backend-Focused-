package com.example.finalproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.ProductDb;
import com.example.finalproject.helper.ReviewDb;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Review;

import java.io.IOException;
import java.util.List;

@WebServlet("/product-details")
public class ProductDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = (String) request.getAttribute("user");

        // Get the id from the URL
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("products");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("products");
            return;
        }

        // fetch the product
        Product product = null;
        try {
            product = ProductDb.getProductById(id);
        } catch (Exception e) {
            System.out.println("[ProductDetailsServlet] Product error: " + e.getMessage());
        }

        // If the product is not available
        if (product == null) {
            response.sendRedirect("products");
            return;
        }

        // Get the reviews for this product
        List<Review> reviews = null;
        try {
            reviews = ReviewDb.getReviewsByProductId(id);
        } catch (Exception e) {
            System.out.println("[ProductDetailsServlet] Reviews error: " + e.getMessage());
        }

        request.setAttribute("product", product);
        request.setAttribute("reviews", reviews);
        request.setAttribute("user", user);

        request.getRequestDispatcher("product-details.jsp").forward(request, response);
    }
}