package com.example.finalproject.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.ReviewDb;

import java.io.IOException;

@WebServlet("/add-review")
public class AddReviewServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get the user from the AuthFilter
        String user = (String) request.getAttribute("user");

        // Verify data
        String idStr = request.getParameter("productId");
        String comment = request.getParameter("comment");
        String ratingStr = request.getParameter("rating");

        // Check the productId
        if (idStr == null || idStr.trim().isEmpty()) {
            System.out.println("[AddReviewServlet] Missing productId from user: " + user);
            response.sendRedirect("products?error=missing_product_id");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("[AddReviewServlet] Invalid productId: " + idStr);
            response.sendRedirect("products?error=invalid_product_id");
            return;
        }

        // Check the comment
        if (comment == null || comment.trim().isEmpty()) {
            System.out.println("[AddReviewServlet] Empty comment from user: " + user);
            response.sendRedirect("product-details?id=" + productId + "&error=comment_required");
            return;
        }

        // Check the rating (must be from 1 to 5)

        if (ratingStr == null || ratingStr.trim().isEmpty()) {
            System.out.println("[AddReviewServlet] Missing rating from user: " + user);
            response.sendRedirect("product-details?id=" + productId + "&error=rating_required");
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingStr.trim());
            if (rating < 1 || rating > 5) throw new NumberFormatException("Rating out of range");
        } catch (NumberFormatException e) {
            System.out.println("[AddReviewServlet] Invalid rating value: " + ratingStr);
            response.sendRedirect("product-details?id=" + productId + "&error=rating_must_be_1_to_5");
            return;
        }


        // add Review
        try {
            ReviewDb.addReview(productId, user, comment.trim(), rating);
            System.out.println("[AddReviewServlet] Review added by '" + user + "' for productId=" + productId);
            response.sendRedirect("product-details?id=" + productId);

        } catch (Exception e) {
            System.out.println("[AddReviewServlet] Failed to save review: " + e.getMessage());
            response.sendRedirect("product-details?id=" + productId + "&error=review_save_failed");
        }
    }
}