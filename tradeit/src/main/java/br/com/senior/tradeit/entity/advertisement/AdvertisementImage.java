package br.com.senior.tradeit.entity.advertisement;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "advertisement_images")
@IdClass(AdvertisementImageId.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisementImage {
    @Id
    private Long displayOrder;
    @Id
    private Long advertisementId;
    private UUID imageSlug;
    private String contentType;
}
