package dev.lorenzharfst.managr.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class AccessDeniedFormLoginHandler implements AccessDeniedHandler {

    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
    }
}

