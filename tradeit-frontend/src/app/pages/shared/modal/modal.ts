import { Component, input } from "@angular/core";

@Component({
    selector: "app-modal",
    imports: [],
    templateUrl: "./modal.html",
    styleUrl: "./modal.css",
})
export class Modal {
    public readonly opened = input.required<boolean>();
}
