import { Item } from "../item/item";

export interface Advertisement {
    id: number;
    description: string;
    item: Item;
    tradingItem: Item;
    extraMoneyAmountRequired: number;
    imageUrl: string;
    condition: string;
}
