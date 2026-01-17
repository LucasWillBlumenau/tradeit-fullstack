import { Component, input, output } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: "app-button-and-link",
    imports: [RouterLink],
    templateUrl: "./button-and-link.html",
    styleUrl: "./button-and-link.css",
})
export class ButtonAndLink {
    public readonly buttonClick = output<PointerEvent>();
    public phrase = input.required<string>();
    public linkDescription = input.required<string>();
    public routerLink = input.required<string>();

    onClick(event: PointerEvent): void {
        this.buttonClick.emit(event);
        event.stopPropagation();
    }
}
