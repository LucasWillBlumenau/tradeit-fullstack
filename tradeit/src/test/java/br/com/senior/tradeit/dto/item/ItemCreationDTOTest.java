package br.com.senior.tradeit.dto.item;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class ItemCreationDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank(String name) {
        var itemCreation = new ItemCreationDTO(name, 1L);

        var violations = validator.validate(itemCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullSource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotNullAreNull(Long categoryId) {
        var itemCreation = new ItemCreationDTO("item", categoryId);

        var violations = validator.validate(itemCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("categoryId", violation.getPropertyPath().toString());
    }


}