import { Component, OnInit, signal, inject } from "@angular/core";
import { AdvertisementService } from "../../../services/advertisement-service";
import { AdvertisementsPage } from "../../../model/advertisement/advertisement-page";
import { environment } from "../../../../environments/environment";
import { Card } from "./card/card";

@Component({
    selector: "app-advertisements",
    imports: [Card],
    templateUrl: "./advertisements.html",
    styleUrl: "./advertisements.css",
})
export class Advertisements implements OnInit {
    private readonly advertisementService = inject(AdvertisementService);
    protected readonly advertisementsPage = signal<AdvertisementsPage | null>(null);
    protected readonly apiUrl = environment.apiUrl;

    ngOnInit(): void {
        this.advertisementService.getAdvertisements().subscribe({
            next: (advertisementsPage) => this.advertisementsPage.set(advertisementsPage),
        });
    }
}
