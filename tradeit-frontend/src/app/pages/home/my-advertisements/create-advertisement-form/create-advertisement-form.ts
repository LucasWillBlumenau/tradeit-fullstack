import { Component, ElementRef, inject, OnInit, output, signal, ViewChild } from "@angular/core";
import { ModalFormWrapper } from "../../shared/modal-form-wrapper/modal-form-wrapper";
import { Field } from "../../shared/field/field";
import { Item } from "../../../../model/item/item";
import { ItemService } from "../../../../services/item-service";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { AdvertisementCreation } from "../../../../model/advertisement/advertisement-creation";

@Component({
    selector: "app-create-advertisement-form",
    imports: [ModalFormWrapper, Field, ReactiveFormsModule],
    templateUrl: "./create-advertisement-form.html",
    styleUrl: "./create-advertisement-form.css",
})
export class CreateAdvertisementForm implements OnInit {
    public readonly submittion = output<AdvertisementCreation>();

    @ViewChild("imagesInput")
    private readonly imagesFileInput!: ElementRef;
    @ViewChild("videoInput")
    private readonly videoFileInput!: ElementRef;
    private readonly itemService = inject(ItemService);

    protected readonly items = signal<Item[]>([]);
    protected readonly formGroup: FormGroup;

    protected totalPages = -1;
    protected currentPage = 0;

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            itemId: ["", Validators.required],
            tradingItemId: ["", Validators.required],
            additionalMoneyAmountRequired: ["", Validators.required],
            itemCondition: ["", Validators.required],
            description: ["", Validators.required],
            images: [null],
            video: [null],
        });
    }

    get itemId() {
        return this.formGroup.get("itemId");
    }

    get tradingItemId() {
        return this.formGroup.get("tradingItemId");
    }

    get additionalMoneyAmountRequired() {
        return this.formGroup.get("additionalMoneyAmountRequired");
    }

    get itemCondition() {
        return this.formGroup.get("itemCondition");
    }

    get description() {
        return this.formGroup.get("description");
    }

    get images() {
        return this.formGroup.get("images");
    }

    get video() {
        return this.formGroup.get("video");
    }

    ngOnInit(): void {
        this.fetchNextItemChunk();
    }

    fetchNextItemChunk(): void {
        if (this.totalPages == -1 || this.totalPages > this.currentPage) {
            this.itemService.getItemsFromPage(this.currentPage).subscribe({
                next: (itemPage) => {
                    this.items.update((items) => [...items, ...itemPage.content]);
                    this.currentPage++;
                    this.totalPages = itemPage.totalPages;
                },
            });
        }
    }

    onImagesSelected(): void {
        const files = this.imagesFileInput.nativeElement.files;
        const value = files.length > 0 ? files : null;
        this.images?.setValue(value);
    }

    onVideoSelected(): void {
        const files = this.videoFileInput.nativeElement.files;
        const value = files.length > 0 ? files[0] : null;
        this.video?.setValue(value);
    }

    submitForm() {
        const itemId = this.itemId?.value as number;
        const tradingItemId = this.tradingItemId?.value as number;
        const additionalMoneyAmountRequired = this.additionalMoneyAmountRequired?.value as number;
        const itemCondition = this.itemCondition?.value as string;
        const description = this.description?.value as string;
        const images = this.images?.value as FileList;
        const video = this.video?.value as File;

        const advertisementCreation: AdvertisementCreation = {
            itemId,
            tradingItemId,
            additionalMoneyAmountRequired,
            itemCondition,
            description,
            images,
            video,
        };
        this.submittion.emit(advertisementCreation);
        this.formGroup.reset();
    }
}
