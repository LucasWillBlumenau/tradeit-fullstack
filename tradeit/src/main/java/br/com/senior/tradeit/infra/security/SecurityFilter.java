package br.com.senior.tradeit.infra.security;


import br.com.senior.tradeit.service.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_COOKIE = "X-Authorization";

    private final JWTTokenManager jwtTokenManager;
    private final UserDetailsService userDetailsService;

    public SecurityFilter(JWTTokenManager jwtTokenManager, UserDetailsService userDetailsService) {
        this.jwtTokenManager = jwtTokenManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = searchAuthorizationCookieValue(request.getCookies());
        String username = jwtTokenManager.validateToken(token);
        if (username != null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authenticationToken);
            } catch (UsernameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    private static String searchAuthorizationCookieValue(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (AUTHORIZATION_COOKIE.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
