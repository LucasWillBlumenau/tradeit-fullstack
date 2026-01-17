package br.com.senior.tradeit.service;

import br.com.senior.tradeit.entity.user.Role;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsService userDetailsService;

    @Test
    void loadByUsername_shouldReturnUser_whenUsernameIsFound() {
        User user = new User(1L, "John Doe", "john.doe@mail.com", "password", Role.ROLE_COMMON);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe@mail.com");
        assertInstanceOf(User.class, userDetails);
        assertEquals(user, userDetails);
    }

    @Test
    void loadByUsername_shouldThrowUsernameNotFoundException_whenUserIsNotFound() {
        when(userRepository.findByEmail("john.doe@mail.com")).thenReturn(Optional.empty());
        var exc = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("john.doe@mail.com"));
        assertEquals("usuário não encontrado", exc.getMessage());
    }

}