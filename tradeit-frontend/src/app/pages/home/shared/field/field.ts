import { Component, input } from "@angular/core";

@Component({
    selector: "app-field",
    imports: [],
    templateUrl: "./field.html",
    styleUrl: "./field.css",
})
export class Field {
    public readonly label = input.required<string>();
}
