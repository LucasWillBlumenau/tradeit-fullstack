package br.com.senior.tradeit.dto.advertisement;

import br.com.senior.tradeit.dto.item.ItemDetailsDTO;
import br.com.senior.tradeit.entity.advertisement.AdvertisementStatus;
import br.com.senior.tradeit.entity.condition.ItemCondition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AdvertisementDetailsDTO(
        Long id,
        String description,
        ItemDetailsDTO item,
        ItemDetailsDTO tradingItem,
        LocalDate advertisementDate,
        String advertiserName,
        BigDecimal extraMoneyAmountRequired,
        List<String> imageUrls,
        String videoUrl,
        AdvertisementStatus status,
        ItemCondition condition
) {
}
