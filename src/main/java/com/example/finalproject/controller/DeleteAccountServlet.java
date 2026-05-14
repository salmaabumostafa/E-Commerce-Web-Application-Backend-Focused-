package com.example.finalproject.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.finalproject.helper.UserDb;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String user = (String) request.getAttribute("user");

        try {
            UserDb.deleteUser(user);

            // Clear the session from Redis
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("SESSION_ID".equals(c.getName())) {
                        try (Jedis jedis = new Jedis("localhost", 6379)) {
                            jedis.del("session:" + c.getValue());
                        }
                        break;
                    }
                }
            }

            // Clear the cookie
            Cookie cookie = new Cookie("SESSION_ID", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            System.out.println("[DeleteAccountServlet] Account deleted: " + user);
            response.sendRedirect("login.jsp");

        } catch (Exception e) {
            System.out.println("[DeleteAccountServlet] Error: " + e.getMessage());
            response.sendRedirect("products?error=server_error");
        }
    }
}
