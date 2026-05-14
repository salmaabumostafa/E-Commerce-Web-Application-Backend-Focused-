package com.example.finalproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.UserDb;

import java.io.IOException;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("signup.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            boolean created = UserDb.registerUser(username, password);

            if (created) {
                response.sendRedirect("login.jsp?registered=true");
            } else {
                response.sendRedirect("signup.jsp?error=exists");
            }

        } catch (Exception e) {
            System.out.println("[SignUpServlet] Error: " + e.getMessage());
            response.sendRedirect("signup.jsp?error=server");
        }
    }
}