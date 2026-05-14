package com.example.finalproject.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@WebServlet("/signout")
public class SignOutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Clear the session from Redis
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("SESSION_ID".equals(c.getName())) {
                    try (Jedis jedis = new Jedis("localhost", 6379)) {
                        jedis.del("session:" + c.getValue());
                        System.out.println("[SignOutServlet] Session deleted: " + c.getValue());
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

        response.sendRedirect("login.jsp");
    }
}