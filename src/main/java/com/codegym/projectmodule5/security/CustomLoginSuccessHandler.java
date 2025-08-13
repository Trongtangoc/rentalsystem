package com.codegym.projectmodule5.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);
        log.info("Login successful for user: {} - Redirecting to: {}",
                authentication.getName(), targetUrl);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            log.debug("Checking authority: {}", authority);

            switch (authority) {
                case "ROLE_ADMIN":
                    log.info("User {} has ADMIN role", authentication.getName());
                    return "/admin/dashboard";
                case "ROLE_HOST":
                    log.info("User {} has HOST role", authentication.getName());
                    return "/host/dashboard";
                case "ROLE_USER":
                    log.info("User {} has USER role", authentication.getName());
                    return "/user/dashboard";
            }
        }

        // Default fallback
        log.warn("No specific role found for user {}, defaulting to /user/dashboard",
                authentication.getName());
        return "/user/dashboard";
    }
}