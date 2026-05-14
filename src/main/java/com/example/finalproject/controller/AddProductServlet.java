package com.example.finalproject.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.ProductDb;
import com.example.finalproject.helper.UserDb;

import java.io.IOException;

@WebServlet("/add-product")
public class AddProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get the user from the AuthFilter
        String user = (String) request.getAttribute("user");

        // Check permissions, is it admin or not
        try {
            String role = UserDb.getUserRole(user);
            if (!"admin".equals(role)) {
                System.out.println("[AddProductServlet] Access denied for user: " + user);
                response.sendRedirect("products?error=access_denied");
                return;
            }
        } catch (Exception e) {
            System.out.println("[AddProductServlet] Role check failed for '" + user + "': " + e.getMessage());
            response.sendRedirect("products?error=role_check_failed");
            return;
        }

        // Verify Product data
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");

        if (name == null || name.trim().isEmpty()) {
            System.out.println("[AddProductServlet] Product name is missing");
            response.sendRedirect("products?error=product_name_required");
            return;
        }

        if (priceStr == null || priceStr.trim().isEmpty()) {
            System.out.println("[AddProductServlet] Product price is missing");
            response.sendRedirect("products?error=product_price_required");
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceStr.trim());
            if (price <= 0) throw new NumberFormatException("Price must be positive");
        } catch (NumberFormatException e) {
            System.out.println("[AddProductServlet] Invalid price value: " + priceStr);
            response.sendRedirect("products?error=invalid_price_format");
            return;
        }

        // Add product
        try {
            ProductDb.addProduct(name.trim(), price);
            System.out.println("[AddProductServlet] Product added by '" + user + "': " + name + " @ " + price);
            response.sendRedirect("products");

        } catch (Exception e) {
            System.out.println("[AddProductServlet] Failed to add product '" + name + "': " + e.getMessage());
            response.sendRedirect("products?error=product_save_failed");
        }
    }
}