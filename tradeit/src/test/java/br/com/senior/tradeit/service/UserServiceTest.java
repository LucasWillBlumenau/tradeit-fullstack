package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.user.UserCreationDTO;
import br.com.senior.tradeit.dto.user.UserDetailsDTO;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUser_whenEmailIsNotTaken() {
        UserCreationDTO userCreationDTO = new UserCreationDTO(
                "John Doe",
                "john.doe@mail.com",
                "password"
        );
        when(userRepository.existsByEmail(userCreationDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(userCreationDTO.password())).thenReturn("hashedpassword");

        UserDetailsDTO userDetails = userService.createUser(userCreationDTO);
        assertEquals("John Doe", userDetails.name());
        assertEquals("john.doe@mail.com", userDetails.email());
        verify(userRepository).save(any());
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenEmailIsTaken() {
        UserCreationDTO userCreationDTO = new UserCreationDTO(
                "John Doe",
                "john.doe@mail.com",
                "password"
        );
        when(userRepository.existsByEmail(userCreationDTO.email())).thenReturn(true);
        when(passwordEncoder.encode(userCreationDTO.password())).thenReturn("hashedpassword");

        var exc = assertThrows(BadRequestException.class, () -> userService.createUser(userCreationDTO));
        assertEquals("o email john.doe@mail.com já está em uso", exc.getMessage());
    }

}