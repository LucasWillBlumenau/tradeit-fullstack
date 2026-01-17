package br.com.senior.tradeit.entity.offer;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "offer_images")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "offer_id")
    private Long offerId;
    private UUID imageSlug;
    private String contentType;
}
