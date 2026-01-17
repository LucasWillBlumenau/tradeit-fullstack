package br.com.senior.tradeit.dto.item;

import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemUpdateTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();


    @ParameterizedTest
    @NullSource
    void validate_shouldReturnNotNullViolation_whenFieldsRequiredToBeNotNullAreNull(Long id) {
        ItemUpdateDTO itemUpdate = new ItemUpdateDTO(id, "name", 1L);

        var validations = validator.validate(itemUpdate)
                .stream()
                .toList();
        var validation = validations.get(0);

        assertEquals(1L, validations.size());
        assertEquals("id", validation.getPropertyPath().toString());
    }

}