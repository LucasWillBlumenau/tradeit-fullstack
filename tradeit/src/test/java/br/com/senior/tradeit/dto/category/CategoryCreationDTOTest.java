package br.com.senior.tradeit.dto.category;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class CategoryCreationDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank(String name) {
        var categoryCreationDTO = new CategoryCreationDTO(name);

        var violations = validator.validate(categoryCreationDTO)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("name", violation.getPropertyPath().toString());
    }
}