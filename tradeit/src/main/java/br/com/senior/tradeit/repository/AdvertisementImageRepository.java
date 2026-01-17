package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.advertisement.AdvertisementImage;
import br.com.senior.tradeit.entity.advertisement.AdvertisementImageId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AdvertisementImageRepository extends Repository<AdvertisementImage, AdvertisementImageId> {
    void saveAll(Iterable<AdvertisementImage> images);
    Optional<AdvertisementImage> findByDisplayOrderAndAdvertisementId(Long displayOrder, Long advertisementId);
}
