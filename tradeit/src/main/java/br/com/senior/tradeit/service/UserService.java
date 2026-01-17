package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.user.UserCreationDTO;
import br.com.senior.tradeit.dto.user.UserDetailsDTO;
import br.com.senior.tradeit.entity.user.Role;
import br.com.senior.tradeit.entity.user.User;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetailsDTO createUser(UserCreationDTO userCreationDTO) {
        String name = userCreationDTO.name().trim();
        String email = userCreationDTO.email().trim().toLowerCase();
        String password = Objects.requireNonNull((passwordEncoder.encode(userCreationDTO.password())));

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("o email " + email + " já está em uso");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(Role.ROLE_COMMON)
                .build();
        userRepository.save(user);

        return new UserDetailsDTO(user);
    }
}
