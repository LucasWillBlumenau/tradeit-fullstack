package br.com.senior.tradeit.dto.user;


import br.com.senior.tradeit.validation.user.regex.UserValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreationDTO(
        @NotBlank
        String name,
        @NotNull
        @Pattern(
                regexp = UserValidationPatterns.EMAIL_PATTERN,
                message = "o valor do campo é inválido"
        )
        String email,
        @NotNull
        @Size(min = 8, max = 32)
        String password
) {
}
