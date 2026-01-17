package br.com.senior.tradeit.dto.offer;

import br.com.senior.tradeit.entity.offer.ContactType;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class ContactDTOTest {
    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @NullSource
    void validate_shouldReturnNotNullViolations_whenFieldsRequiredToBeNotNullAreNull(ContactType contactType) {
        var contact = new ContactDTO(contactType, "47999999999");

        var violations = validator.validate(contact)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("contactType", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlankOrNull(String contactInfo) {
        var contact = new ContactDTO(ContactType.WHATSAPP, contactInfo);

        var violations = validator.validate(contact)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("contactInfo", violation.getPropertyPath().toString());
    }
}