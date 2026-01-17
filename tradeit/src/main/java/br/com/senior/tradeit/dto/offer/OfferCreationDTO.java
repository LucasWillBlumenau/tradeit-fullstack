package br.com.senior.tradeit.dto.offer;

import br.com.senior.tradeit.entity.condition.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record OfferCreationDTO(
        @NotBlank
        String description,
        @NotNull
        Long advertisementId,
        @NotNull
        @PositiveOrZero
        BigDecimal additionalMoneyOffer,
        @NotNull
        ItemCondition itemCondition,
        @NotNull
        MultipartFile[] images,
        @NotNull
        MultipartFile video
) {
}
