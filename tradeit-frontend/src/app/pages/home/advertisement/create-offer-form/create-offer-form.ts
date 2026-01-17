import { Component, ElementRef, inject, input, output, ViewChild } from "@angular/core";
import { AdvertisementDetails } from "../../../../model/advertisement/advertisement-details";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { OfferCreation } from "../../../../model/offer/offer-creation";
import { CurrencyPipe } from "@angular/common";
import { Field } from "../../shared/field/field";
import { ModalFormWrapper } from "../../shared/modal-form-wrapper/modal-form-wrapper";

@Component({
    selector: "app-create-offer-form",
    imports: [ReactiveFormsModule, CurrencyPipe, Field, ModalFormWrapper],
    templateUrl: "./create-offer-form.html",
    styleUrl: "./create-offer-form.css",
})
export class CreateOfferForm {
    public readonly advertisement = input.required<AdvertisementDetails | null>();
    public readonly submittion = output<OfferCreation>();

    protected formGroup: FormGroup;

    @ViewChild("imagesInput")
    private imagesFileInput!: ElementRef;
    @ViewChild("videoInput")
    private videoFileInput!: ElementRef;

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            condition: ["", Validators.required],
            additionalMoneyOffer: ["", Validators.required, Validators.min(50)],
            description: ["", Validators.required],
            images: [null, Validators.nullValidator],
            video: [null, Validators.nullValidator],
        });
    }

    get condition() {
        return this.formGroup.get("condition");
    }

    get additionalMoneyOffer() {
        return this.formGroup.get("additionalMoneyOffer");
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

    submitOffer(): void {
        // TODO: adjust form validation
        const itemCondition = this.condition?.value as string;
        const additionalMoneyOffer = this.additionalMoneyOffer?.value as string;
        const description = this.description?.value as string;
        const images = this.images?.value as FileList;
        const video = this.video?.value as File;

        const offerCreation: OfferCreation = { itemCondition, additionalMoneyOffer, description, images, video };
        this.submittion.emit(offerCreation);
        this.formGroup.reset();
    }
}
