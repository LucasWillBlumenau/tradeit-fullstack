package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.user.UserLoginDTO;
import br.com.senior.tradeit.infra.security.JWTTokenManager;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenManager jwtTokenManager;

    public AuthenticationService(AuthenticationManager authenticationManager, JWTTokenManager jwtTokenManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    public Cookie authenticateUser(UserLoginDTO userLogin) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userLogin.email(),
                userLogin.password()
        );
        authenticationManager.authenticate(authenticationToken);
        String token = jwtTokenManager.createToken(userLogin.email());

        Cookie cookie = new Cookie("X-Authorization", token);
        cookie.setMaxAge(2 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setAttribute("same-site", "none");
        cookie.setPath("/");
        return cookie;
    }
}
