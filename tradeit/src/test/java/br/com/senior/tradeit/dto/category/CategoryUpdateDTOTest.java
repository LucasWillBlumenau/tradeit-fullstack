package br.com.senior.tradeit.dto.category;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class CategoryUpdateDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @NullSource
    void validate_shouldReturnNotNullViolations_whenFieldsRequiredToBeNotNullAreNull(Long id) {
        CategoryUpdateDTO categoryUpdate = new CategoryUpdateDTO(id, "name");

        var validations = validator.validate(categoryUpdate)
                .stream()
                .toList();
        var validation = validations.get(0);

        assertEquals(1, validations.size());
        assertEquals("id", validation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolation_whenFieldsRequiredToBeNotBlankAreBlank(String name) {
        CategoryUpdateDTO categoryUpdate = new CategoryUpdateDTO(1L, name);

        var validations = validator.validate(categoryUpdate)
                .stream()
                .toList();
        var validation = validations.get(0);

        assertEquals(1, validations.size());
        assertEquals("name", validation.getPropertyPath().toString());
    }


}