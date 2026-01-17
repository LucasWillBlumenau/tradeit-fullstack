import { Component, input } from "@angular/core";

@Component({
    selector: "app-input-wrapper",
    imports: [],
    templateUrl: "./input-wrapper.html",
    styleUrl: "./input-wrapper.css",
})
export class InputWrapper {
    public readonly label = input.required<string>();
}
