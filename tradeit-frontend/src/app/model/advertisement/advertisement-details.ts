export interface AdvertisementDetails {
    id: number;
    description: string;
    item: {
        id: number;
        name: string;
        categoryId: number;
    };
    tradingItem: {
        id: number;
        name: string;
        categoryId: number;
    };
    advertisementDate: string;
    advertiserName: string;
    extraMoneyAmountRequired: number;
    imageUrls: string[];
    videoUrl: string;
    status: string;
    condition: string;
}
