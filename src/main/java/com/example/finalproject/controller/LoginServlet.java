package com.example.finalproject.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.UserDb;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // Secret key used to sign and verify JWT tokens
    private static final String SECRET = "secret";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {

            // If invalid, redirect back to login page with an error flag

            if (!UserDb.validateUser(username, password)) {
                response.sendRedirect("login.jsp?error=invalid");
                return;
            }

            // Session-Based Auth (Stateful)
            String sessionId = UUID.randomUUID().toString();
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                jedis.setex("session:" + sessionId, 1800, username);
                System.out.println("[LoginServlet] Session created: " + sessionId);
            }

            Cookie sessionCookie = new Cookie("SESSION_ID", sessionId);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true); // Not accessible via JavaScript
            sessionCookie.setMaxAge(1800); //expiring after 30 minutes
            response.addCookie(sessionCookie);

            //Token-Based Auth (Stateless)
            String token = JWT.create()
                    .withClaim("user", username)
                    .sign(Algorithm.HMAC256(SECRET));

            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(1800);
            response.addCookie(jwtCookie);

            System.out.println("[LoginServlet] JWT issued for: " + username);

            response.sendRedirect("products");

        } catch (Exception e) {
            System.out.println("[LoginServlet] Error: " + e.getMessage());
            response.sendRedirect("login.jsp?error=server");
        }
    }
}