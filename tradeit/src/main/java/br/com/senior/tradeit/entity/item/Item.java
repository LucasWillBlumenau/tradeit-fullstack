package br.com.senior.tradeit.entity.item;

import br.com.senior.tradeit.entity.category.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private Boolean isActive;

    @Override
    public String toString() {
        return String.format("%d - %s", id, name);
    }
}
