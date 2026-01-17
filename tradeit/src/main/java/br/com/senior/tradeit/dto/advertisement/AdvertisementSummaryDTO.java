package br.com.senior.tradeit.dto.advertisement;

import br.com.senior.tradeit.dto.item.ItemDetailsDTO;
import br.com.senior.tradeit.entity.condition.ItemCondition;

import java.math.BigDecimal;

public record AdvertisementSummaryDTO(
        Long id,
        String description,
        ItemDetailsDTO item,
        ItemDetailsDTO tradingItem,
        BigDecimal extraMoneyAmountRequired,
        String imageUrl,
        ItemCondition condition
) {
}
