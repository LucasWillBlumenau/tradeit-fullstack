package br.com.senior.tradeit.dto.advertisement;

import br.com.senior.tradeit.entity.condition.ItemCondition;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class AdvertisementCreationDTOTest {

    private final Validator validator = buildDefaultValidatorFactory()
            .getValidator();

    @ParameterizedTest
    @MethodSource
    void validate_shouldReturnNotNullViolations_whenSettingNullFields(
            Long itemId,
            ItemCondition itemCondition,
            BigDecimal extraMoneyAmountRequired,
            Long tradingItemId,
            MultipartFile[] images,
            MultipartFile video,
            String field
    ) {
        AdvertisementCreationDTO advertisementCreation = new AdvertisementCreationDTO(
                "description",
                itemId,
                itemCondition,
                extraMoneyAmountRequired,
                tradingItemId,
                images,
                video
        );

        var violations = validator.validate(advertisementCreation)
                .stream()
                .toList();
        var violation = violations.get(0);

        assertEquals(1, violations.size());
        assertEquals(field, violation.getPropertyPath().toString());
   }

   static Stream<Arguments> validate_shouldReturnNotNullViolations_whenSettingNullFields() {
        MultipartFile[] images = new MultipartFile[0];
        MultipartFile video = new MockMultipartFile("file", new byte[0]);
        return Stream.of(
                Arguments.of(null, ItemCondition.NEW, BigDecimal.ZERO, 1L, images, video, "itemId"),
                Arguments.of(1L, null, BigDecimal.ZERO, 1L, images, video, "itemCondition"),
                Arguments.of(1L, ItemCondition.NEW, null, 1L, images, video, "extraMoneyAmountRequired"),
                Arguments.of(1L, ItemCondition.NEW, BigDecimal.ZERO, null, images, video, "tradingItemId"),
                Arguments.of(1L, ItemCondition.NEW, BigDecimal.ZERO, 1L, null, video, "images"),
                Arguments.of(1L, ItemCondition.NEW, BigDecimal.ZERO, 1L, images, null, "video")
        );
   }

   @ParameterizedTest
   @NullAndEmptySource
   void validate_shouldReturnNotBlankViolations(String description) {
       MultipartFile[] images = new MultipartFile[0];
       MultipartFile video = new MockMultipartFile("file", new byte[0]);
       var advertisementCreation = new AdvertisementCreationDTO(
               description,
               1L,
               ItemCondition.NEW,
               BigDecimal.ZERO,
               1L,
               images,
               video
       );

       var violations = validator.validate(advertisementCreation)
               .stream()
               .toList();
       var violation = violations
               .get(0);

       assertEquals(1, violations.size());
       assertEquals("description", violation.getPropertyPath().toString());
   }

}