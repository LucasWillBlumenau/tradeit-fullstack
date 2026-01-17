import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: "app-card",
    imports: [RouterLink],
    templateUrl: "./card.html",
    styleUrl: "./card.css",
})
export class Card {
    public readonly itemId = input.required<number>();
    public readonly imageUrl = input.required<string>();
    public readonly itemName = input.required<string>();
    public readonly tradingItemName = input.required<string>();
}
