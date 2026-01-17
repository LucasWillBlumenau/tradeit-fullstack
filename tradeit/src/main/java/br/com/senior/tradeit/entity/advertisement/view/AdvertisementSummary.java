package br.com.senior.tradeit.entity.advertisement.view;


import br.com.senior.tradeit.entity.condition.ItemCondition;

import java.math.BigDecimal;

public interface AdvertisementSummary {
    Long getId();
    String getDescription();
    Long getItemId();
    String getItemName();
    Long getCategoryId();
    Long getDisplayOrder();
    Long getTradingItemId();
    String getTradingItemName();
    Long getTradingCategoryId();
    ItemCondition getItemCondition();
    BigDecimal getExtraMoneyAmountRequired();
    void setId(Long id);
    void setDescription(String description);
    void setItemId(Long itemId);
    void setItemName(String itemName);
    void setCategoryId(Long categoryId);
    void setDisplayOrder(Long displayOrder);

}
