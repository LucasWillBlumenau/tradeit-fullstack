package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.user.UserLoginDTO;
import br.com.senior.tradeit.infra.security.JWTTokenManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTTokenManager jwtTokenManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void authenticateUser_shouldReturnJWTToken_whenUserProvidesCorrectCredentials() {
        UserLoginDTO user = new UserLoginDTO("myusername@mail.com", "password");
        authenticationService.authenticateUser(user);

        verify(jwtTokenManager).createToken("myusername@mail.com");
    }

    @Test
    public void authenticateUser_shouldThrowAuthorizationException_whenUserProvidesWrongCredentials() {
        UserLoginDTO user = new UserLoginDTO("myusername@mail.com", "wrongpassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(AuthenticationCredentialsNotFoundException.class);

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> authenticationService.authenticateUser(user));
    }

}