import { Component, inject, output } from "@angular/core";
import { Field } from "../../shared/field/field";
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ContacyType } from "../../../../model/offer/contact-type";
import { ContactInfo } from "../../../../model/offer/contact-info";

@Component({
    selector: "app-accept-offer-form",
    imports: [Field, ReactiveFormsModule],
    templateUrl: "./accept-offer-form.html",
    styleUrl: "./accept-offer-form.css",
})
export class AcceptOfferForm {
    public readonly acceptOffer = output<ContactInfo>();

    protected readonly formGroup: FormGroup;

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            contactType: [""],
            contactInfo: [""],
        });
    }

    get contactType() {
        return this.formGroup.get("contactType");
    }

    get contactInfo() {
        return this.formGroup.get("contactInfo");
    }

    submitForm() {
        const contactType = this.contactType?.value as ContacyType;
        const contactInfo = this.contactInfo?.value as string;

        this.acceptOffer.emit({ contactType, contactInfo });
    }
}
