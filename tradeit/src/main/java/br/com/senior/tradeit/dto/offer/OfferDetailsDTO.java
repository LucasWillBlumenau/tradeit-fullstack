package br.com.senior.tradeit.dto.offer;

import br.com.senior.tradeit.entity.condition.ItemCondition;
import br.com.senior.tradeit.entity.offer.OfferStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record OfferDetailsDTO(
        Long id,
        Long itemId,
        String itemName,
        ItemCondition itemCondition,
        BigDecimal additionalMoneyOffer,
        String description,
        OfferStatus offerStatus,
        List<String> imageUrls,
        String videoUrl,
        String madeBy,
        Long advertisementId,
        OfferStatus status,
        Optional<ContactDTO> contact,
        String advertisementImageUrl
) {
}
