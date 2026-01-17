import { Component, input, output } from "@angular/core";
import { Advertisement } from "../../../../model/advertisement/advertisement";
import { CurrencyPipe } from "@angular/common";
import { TranslateItemConditionPipe } from "../../../../core/pipes/translate-item-condition-pipe";
import { environment } from "../../../../../environments/environment";

@Component({
    selector: "app-advertisement-info",
    imports: [CurrencyPipe, TranslateItemConditionPipe],
    templateUrl: "./advertisement-info.html",
    styleUrl: "./advertisement-info.css",
})
export class AdvertisementInfo {
    public advertisementCancelling = output();
    public advertisementOffersView = output();

    protected apiUrl = environment.apiUrl;
    public advertisement = input.required<Advertisement>();

    onAdvertisementCancellingClick() {
        this.advertisementCancelling.emit();
    }

    onOffersViewClick() {
        this.advertisementOffersView.emit();
    }
}
