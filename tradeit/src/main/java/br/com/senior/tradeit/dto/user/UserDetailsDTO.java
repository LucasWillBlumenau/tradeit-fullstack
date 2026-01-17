package br.com.senior.tradeit.dto.user;

import br.com.senior.tradeit.entity.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDetailsDTO(
        Long id,
        String name,
        String email
) {

    public UserDetailsDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
