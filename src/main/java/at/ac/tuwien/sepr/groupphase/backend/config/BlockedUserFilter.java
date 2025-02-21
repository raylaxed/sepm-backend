package at.ac.tuwien.sepr.groupphase.backend.config;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BlockedUserFilter extends OncePerRequestFilter {

    private final UserService userService;

    public BlockedUserFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            try {
                ApplicationUser user = userService.findApplicationUserByEmail(email);
                if (user.getBlocked()) {
                    new SecurityContextLogoutHandler().logout(request, response, authentication);

                    // Add CORS headers to the response
                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "*");

                    // Send HTTP 401 (Unauthorized) with a custom header or JSON payload
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("Location", "/"); // Optional: frontend can read this header
                    response.getWriter().write("{\"message\":\"User is blocked\"}");
                    return;
                }
            } catch (NotFoundException e) {
                // Log the error and proceed
            }
        }
        filterChain.doFilter(request, response);
    }

}