package com.example.finalproject.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final String SECRET = "secret";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        System.out.println("[AuthFilter] REQUEST: " + path);

        // Allow pages without login
        if (path.startsWith("/login") ||
                path.startsWith("/signup") ||
                path.endsWith(".css") ||
                path.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        String user = null;

        //SESSION CHECK (Redis)

        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("SESSION_ID".equals(c.getName())) {
                    sessionId = c.getValue();
                    break;
                }
            }
        }

        if (sessionId != null) {
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                user = jedis.get("session:" + sessionId);
            }
        }

        //JWT CHECK

        if (user == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET))
                            .build()
                            .verify(token);
                    user = jwt.getClaim("user").asString();
                } catch (Exception e) {
                    user = null;
                }
            }
        }


        //AUTH RESULT

        if (user == null) {
            System.out.println("[AuthFilter] UNAUTHORIZED: " + path);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.setAttribute("user", user);
        chain.doFilter(request, response);
    }
}