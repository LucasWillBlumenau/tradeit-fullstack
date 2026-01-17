package br.com.senior.tradeit.entity.offer.view;

import java.util.UUID;

public interface OfferVideoSummary {
    Long getUserId();
    Long getAdvertiserId();
    UUID getVideoSlug();
    String getContentType();
}
