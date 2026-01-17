export interface AdvertisementCreation {
    itemId: number;
    tradingItemId: number;
    additionalMoneyAmountRequired: number;
    itemCondition: string;
    description: string;
    images: FileList;
    video: File;
}
