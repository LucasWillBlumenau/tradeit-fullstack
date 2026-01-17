package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.offer.Offer;
import br.com.senior.tradeit.entity.offer.view.OfferVideoSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends Repository<Offer, Long> {
    void save(Offer offer);
    List<Offer> findByAdvertisementId(Long advertisementId);
    Optional<Offer> findById(Long id);
    @Modifying
    @Query("""
            UPDATE
                Offer
            SET
                status = 'DENIED'
            WHERE
                status = 'PENDING'
                AND advertisement.id = :advertisementId
                AND id <> :offerId
            """)
    void setOtherPendingAdvertisementOffersStatusToDenied(Long advertisementId, Long offerId);
    @Query("""
            SELECT
                o.user.id AS userId,
                a.advertiser.id AS advertiserId,
                o.videoSlug AS videoSlug,
                o.videoContentType AS contentType
            FROM
                Offer o
            INNER JOIN
                Advertisement a
            ON
                a.id = o.advertisement.id
            WHERE
                o.id = :id
            """)
    Optional<OfferVideoSummary> findVideoByOfferId(Long id);

    Page<Offer> findByUserId(Long id, Pageable pageable);
}
