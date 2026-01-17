package br.com.senior.tradeit.dto.offer;

import br.com.senior.tradeit.entity.condition.ItemCondition;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class OfferCreationDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();


    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldReturnNotBlankViolations_whenFieldsRequiredToBeNotBlankAreBlank(String description) {
        MultipartFile[] images = {};
        MultipartFile video = new MockMultipartFile("file", new byte[0]);
        var itemCreation = new OfferCreationDTO(
                description,
                1L,
                BigDecimal.ZERO,
                ItemCondition.NEW,
                images,
                video
        );

        var violations = validator.validate(itemCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource
    void validate_shouldReturnNullViolations_whenFieldsRequiredToBeNotNullAreNull(
            Long advertisementId,
            BigDecimal additionalMoneyOffer,
            ItemCondition itemCondition,
            MultipartFile[] images,
            MultipartFile video,
            String field
    ) {
        OfferCreationDTO offerCreation = new OfferCreationDTO(
                "description",
                advertisementId,
                additionalMoneyOffer,
                itemCondition,
                images,
                video
        );

        var violations = validator.validate(offerCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals(field, violation.getPropertyPath().toString());
    }

    static Stream<Arguments> validate_shouldReturnNullViolations_whenFieldsRequiredToBeNotNullAreNull() {
        MultipartFile[] images = {};
        MultipartFile video = new MockMultipartFile("file", new byte[0]);
        return Stream.of(
                Arguments.of(null, BigDecimal.ZERO, ItemCondition.NEW, images, video, "advertisementId"),
                Arguments.of(1L, null, ItemCondition.NEW, images, video, "additionalMoneyOffer"),
                Arguments.of(1L, BigDecimal.ZERO, null, images, video, "itemCondition"),
                Arguments.of(1L, BigDecimal.ZERO, ItemCondition.NEW, null, video, "images"),
                Arguments.of(1L, BigDecimal.ZERO, ItemCondition.NEW, images, null, "video")
        );
    }


    @ParameterizedTest
    @CsvSource({
            "-10",
            "-1",
            "-0.01"
    })
    void validate_shouldReturnPositiveOrEqualToZeroViolation_whenFieldsRequiredToBePositiveOrEqualToZeroAreNegative(
            BigDecimal value
    ) {
        MultipartFile[] images = {};
        MultipartFile video = new MockMultipartFile("file", new byte[0]);
        var itemCreation = new OfferCreationDTO(
                "description",
                1L,
                value,
                ItemCondition.NEW,
                images,
                video
        );

        var validations = validator.validate(itemCreation)
                .stream()
                .toList();
        var validation = validations.get(0);

        assertEquals(1, validations.size());
        assertEquals("additionalMoneyOffer", validation.getPropertyPath().toString());
    }

}