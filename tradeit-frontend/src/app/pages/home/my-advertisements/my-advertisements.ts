import { Component, HostListener, inject, OnInit, signal } from "@angular/core";
import { AdvertisementInfo } from "./advertisement-info/advertisement-info";
import { AdvertisementService } from "../../../services/advertisement-service";
import { Advertisement } from "../../../model/advertisement/advertisement";
import { Modal } from "../../shared/modal/modal";
import { CreateAdvertisementForm } from "./create-advertisement-form/create-advertisement-form";
import { AdvertisementCreation } from "../../../model/advertisement/advertisement-creation";
import { AdvertisementOffers } from "./advertisement-offers/advertisement-offers";

@Component({
    selector: "app-my-advertisements",
    imports: [AdvertisementInfo, Modal, CreateAdvertisementForm, AdvertisementOffers],
    templateUrl: "./my-advertisements.html",
    styleUrl: "./my-advertisements.css",
})
export class MyAdvertisements implements OnInit {
    protected readonly createAdvertisementModalIsOpened = signal<boolean>(false);
    protected readonly listAdvertisementOffersModal = signal<boolean>(false);
    protected readonly selectedAdvertisement = signal<number>(0);

    private readonly advertisementService = inject(AdvertisementService);
    protected readonly advertisements = signal<Advertisement[] | null>(null);

    ngOnInit(): void {
        this.loadAdvertisements();
    }

    createAdvertisement(advertisementCreation: AdvertisementCreation) {
        this.advertisementService.createAdvertisement(advertisementCreation).subscribe({
            next: () => {
                // TODO: adicionar aviso indicando que o anúncio foi adicionado com sucesso
                this.createAdvertisementModalIsOpened.set(false);
            },
        });
    }

    openCreateAdvertisementModal() {
        this.createAdvertisementModalIsOpened.set(true);
    }

    @HostListener("window:keyup", ["$event"])
    closeModals(event: KeyboardEvent): void {
        if (event.key === "Escape") {
            this.createAdvertisementModalIsOpened.set(false);
            this.listAdvertisementOffersModal.set(false);
        }
    }

    viewOffers(advertisement: Advertisement) {
        this.selectedAdvertisement.set(advertisement.id);
        this.listAdvertisementOffersModal.set(true);
    }

    cancelAdvertisemnt(advertisement: Advertisement) {
        this.advertisementService.cancelAdvertisement(advertisement.id).subscribe({
            next: () => this.loadAdvertisements(),
        });
    }

    loadAdvertisements(): void {
        this.advertisementService.getUserAdvertisements().subscribe({
            next: (advertisements) => {
                // Adicionar status do anúncio e não filtrar apenas os que estão ativos
                this.advertisements.set(advertisements);
            },
        });
    }
}
