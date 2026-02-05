package dev.lorenzharfst.managr.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class FailureFormLoginHandler implements AuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) {
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}

