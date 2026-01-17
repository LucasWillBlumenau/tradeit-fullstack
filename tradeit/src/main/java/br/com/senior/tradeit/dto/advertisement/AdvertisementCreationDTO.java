package br.com.senior.tradeit.dto.advertisement;

import br.com.senior.tradeit.entity.condition.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public record AdvertisementCreationDTO(
        @NotBlank
        String description,
        @NotNull
        Long itemId,
        @NotNull
        ItemCondition itemCondition,
        @NotNull
        BigDecimal extraMoneyAmountRequired,
        @NotNull
        Long tradingItemId,
        @NotNull
        MultipartFile[] images,
        @NotNull
        MultipartFile video
) {
}
