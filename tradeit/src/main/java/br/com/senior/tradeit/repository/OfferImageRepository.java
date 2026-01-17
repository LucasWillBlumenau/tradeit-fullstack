package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.offer.OfferImage;
import br.com.senior.tradeit.entity.offer.view.OfferImageSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;


public interface OfferImageRepository extends Repository<OfferImage, Long> {
    @Query("""
            SELECT
                o.user.id AS userId,
                a.advertiser.id AS advertiserId,
                oi.imageSlug AS imageSlug,
                oi.contentType AS contentType
            FROM
                Offer o
            INNER JOIN
                OfferImage oi
            ON
                oi.offerId = o.id
            INNER JOIN
                Advertisement a
            ON
                a.id = o.advertisement.id
            WHERE
                oi.id = :imageId
            """)
    Optional<OfferImageSummary> findOfferImageById(Long imageId);
    void saveAll(Iterable<OfferImage> offerImages);
}
