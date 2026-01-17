package br.com.senior.tradeit.entity.advertisement;

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
@Table(name = "advertisements")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "advertiser_id", referencedColumnName = "id")
    private User advertiser;
    @ManyToOne
    @JoinColumn(name = "trading_item_id", referencedColumnName = "id")
    private Item tradingItem;
    private BigDecimal extraMoneyAmountRequired;
    private String description;
    @Enumerated(EnumType.STRING)
    private AdvertisementStatus status;
    @OneToMany
    @JoinColumn(name = "advertisement_id", referencedColumnName = "id")
    private List<AdvertisementImage> images;
    @Enumerated(EnumType.STRING)
    private ItemCondition itemCondition;
    private UUID videoSlug;
    private String videoContentType;
    private LocalDateTime createdAt;
}
