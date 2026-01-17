package br.com.senior.tradeit.entity.offer;

import br.com.senior.tradeit.entity.advertisement.Advertisement;
import br.com.senior.tradeit.entity.condition.ItemCondition;
import br.com.senior.tradeit.entity.item.Item;
import br.com.senior.tradeit.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "advertisement_id", referencedColumnName = "id")
    private Advertisement advertisement;
    private String description;
    private UUID videoSlug;
    private String videoContentType;
    private BigDecimal additionalMoneyOffer;
    @Enumerated(EnumType.STRING)
    private OfferStatus status;
    @Enumerated(EnumType.STRING)
    private ItemCondition itemCondition;
    @OneToMany
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    private List<OfferImage> images;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private ContactType contactType;
    private String contactInfo;
}
