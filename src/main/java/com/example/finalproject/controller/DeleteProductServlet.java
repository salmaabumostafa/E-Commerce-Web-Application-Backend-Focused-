package com.example.finalproject.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.ProductDb;
import com.example.finalproject.helper.UserDb;

import java.io.IOException;

@WebServlet("/delete-product")
public class DeleteProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String user = (String) request.getAttribute("user");

        //Ensure that it's an admin
        try {
            String role = UserDb.getUserRole(user);
            if (!"admin".equals(role)) {
                response.sendRedirect("products");
                return;
            }
        } catch (Exception e) {
            System.out.println("[DeleteProductServlet] Role error: " + e.getMessage());
            response.sendRedirect("products");
            return;
        }

        //fetch the id
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("products?error=invalid_input");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect("products?error=invalid_id");
            return;
        }

        // remove the product
        try {
            ProductDb.deleteProduct(id);
        } catch (Exception e) {
            System.out.println("[DeleteProductServlet] Error: " + e.getMessage());
            response.sendRedirect("products?error=server_error");
            return;
        }

        response.sendRedirect("products");
    }
}