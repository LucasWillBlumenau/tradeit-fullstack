package br.com.senior.tradeit.dto.user;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserLoginDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();


    @ParameterizedTest
    @MethodSource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank(
            String email,
            String password,
            String field
    ) {
        UserLoginDTO userLogin = new UserLoginDTO(email, password);

        var violations = validator.validate(userLogin)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals(field, violation.getPropertyPath().toString());
    }

    static Stream<Arguments> validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank() {
        return Stream.of(
                Arguments.of("", "secretpassword", "email"),
                Arguments.of(null, "secretpassword", "email"),
                Arguments.of("john.doe@mail.com", "", "password"),
                Arguments.of("john.doe@mail.com", null, "password")
        );
    }
}