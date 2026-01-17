package br.com.senior.tradeit.controller;


import br.com.senior.tradeit.dto.user.JWTToken;
import br.com.senior.tradeit.dto.user.UserCreationDTO;
import br.com.senior.tradeit.dto.user.UserDetailsDTO;
import br.com.senior.tradeit.dto.user.UserLoginDTO;
import br.com.senior.tradeit.service.AuthenticationService;
import br.com.senior.tradeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDetailsDTO> signUp(@RequestBody @Valid UserCreationDTO userCreationDTO) {
        UserDetailsDTO userDetails = userService.createUser(userCreationDTO);
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid UserLoginDTO userLogin, HttpServletResponse response) {
        Cookie cookie = authenticationService.authenticateUser(userLogin);
        response.addCookie(cookie);
        return ResponseEntity.noContent()
                .build();
    }
}
