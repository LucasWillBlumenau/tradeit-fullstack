package br.com.senior.tradeit.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
