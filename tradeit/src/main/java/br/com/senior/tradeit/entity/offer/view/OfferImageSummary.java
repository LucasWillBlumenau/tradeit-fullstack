package br.com.senior.tradeit.entity.offer.view;

import java.util.UUID;

public interface OfferImageSummary {
    Long getUserId();
    Long getAdvertiserId();
    UUID getImageSlug();
    String getContentType();
}
