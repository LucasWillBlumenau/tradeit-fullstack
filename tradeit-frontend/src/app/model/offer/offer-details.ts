export interface OfferDetails {
    id: number;
    itemId: number;
    itemName: string;
    itemCondition: string;
    additionalMoneyOffer: number;
    description: string;
    offerStatus: string;
    imageUrls: string[];
    videoUrl: string;
    madeBy: string;
    advertisementId: number;
    status: string;
    contact: {
        contactType: string;
        contactInfo: string;
    } | null;
    advertisementImageUrl: string;
}
