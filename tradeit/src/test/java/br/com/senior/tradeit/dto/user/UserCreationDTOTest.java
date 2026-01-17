package br.com.senior.tradeit.dto.user;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserCreationDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank(String name) {
        UserCreationDTO userCreation = new UserCreationDTO(name, "john@doe.com", "password");

        var violations = validator.validate(userCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "john",
            "john.doe",
            "john.doe@",
            "john.doe@mail",
            "john.doe@mail.com.",
            "john.doe@mail.com.",
            "john.doe.santos@mail.com",
            "@mail.com"
    })
    void validate_shouldReturnViolations_whenEmailIsInvalid(String email) {
        UserCreationDTO userCreation = new UserCreationDTO("John Doe", email, "password");

        var violations = validator.validate(userCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "john.doe@mail.com",
            "john.doe@mail.com.br",
            "j@mail.com",
            "john@mail.com",
            "john@mail.com.br",
    })
    void validate_shouldReturnNoViolation_whenEmailIsValid(String email) {
        UserCreationDTO userCreation = new UserCreationDTO("John Doe", email, "password");

        var violations = validator.validate(userCreation)
                .stream()
                .toList();

        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "1",
            "12",
            "123",
            "1234",
            "12345",
            "123456",
            "1234567",
            "1234567890abcdefghijklmnopqrstuvw",
            "1234567890abcdefghijklmnopqrstuvw!",
    })
    void validate_shouldReturnViolations_whenPasswordDoesNotContainEightDigits(String password) {
        UserCreationDTO userCreation = new UserCreationDTO("John Doe", "john.doe@mail.com", password);

        var violations = validator.validate(userCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("password", violation.getPropertyPath().toString());
    }
}