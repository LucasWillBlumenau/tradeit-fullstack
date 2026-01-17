package br.com.senior.tradeit.entity.advertisement;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"displayOrder", "advertisementId"})
public class AdvertisementImageId {
    private Long displayOrder;
    @Column(name = "advertisement_id")
    private Long advertisementId;
}
