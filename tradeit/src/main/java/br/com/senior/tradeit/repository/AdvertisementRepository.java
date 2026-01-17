package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.advertisement.Advertisement;
import br.com.senior.tradeit.entity.advertisement.view.AdvertisementSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface AdvertisementRepository extends Repository<Advertisement, Long> {
    void save(Advertisement advertisement);

    @Query("""
            SELECT
                a.id AS id,
                a.description AS description,
                MIN(ai.displayOrder) AS displayOrder,
                i.id AS itemId,
                i.name AS itemName,
                i.category.id AS categoryId,
                ti.id AS tradingItemId,
                ti.name AS tradingItemName,
                ti.category.id AS tradingCategoryId,
                a.extraMoneyAmountRequired AS extraMoneyAmountRequired,
                a.itemCondition AS itemCondition
            FROM
                Advertisement a
            LEFT JOIN
                AdvertisementImage ai
            ON
                ai.advertisementId = a.id
            LEFT JOIN
                Item i
            ON
                i.id = a.item.id
            LEFT JOIN
                Item ti
            ON
                ti.id = a.tradingItem.id
            WHERE
                a.status = 'ACTIVE'
            GROUP BY
                a.id,
                i.id,
                ti.id
            """)
    Page<AdvertisementSummary> findAllActive(Pageable pageable);
    Optional<Advertisement> findById(Long id);
    @Query("""
            SELECT
                a.id AS id,
                a.description AS description,
                MIN(ai.displayOrder) AS displayOrder,
                i.id AS itemId,
                i.name AS itemName,
                i.category.id AS categoryId,
                ti.id AS tradingItemId,
                ti.name AS tradingItemName,
                ti.category.id AS tradingCategoryId,
                a.extraMoneyAmountRequired AS extraMoneyAmountRequired,
                a.itemCondition AS itemCondition
            FROM
                Advertisement a
            LEFT JOIN
                AdvertisementImage ai
            ON
                ai.advertisementId = a.id
            LEFT JOIN
                Item i
            ON
                i.id = a.item.id
            LEFT JOIN
                Item ti
            ON
                ti.id = a.tradingItem.id
            WHERE
                a.status = 'ACTIVE' AND
                a.advertiser.id = :userId
            GROUP BY
                a.id,
                i.id,
                ti.id
            """)
    List<AdvertisementSummary> findByAdvertiserId(Long userId);
}
