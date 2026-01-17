import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { Resource } from "../../../../model/resource/resource";

@Component({
    selector: "app-slide",
    imports: [],
    templateUrl: "./slide.html",
    styleUrl: "./slide.css",
})
export class Slide {
    @ViewChild("slide")
    private slide!: ElementRef;

    public resources = input.required<Resource[]>();
    protected lastIndex = computed(() => this.resources().length - 1);
    protected currentIndex = signal<number>(0);

    moveRigth() {
        if (this.lastIndex() > this.currentIndex()) {
            this.currentIndex.update((index) => index + 1);
            this.moveImageToCurrentPosition();
        }
    }

    moveLeft() {
        if (this.currentIndex() > 0) {
            this.currentIndex.update((index) => index - 1);
            this.moveImageToCurrentPosition();
        }
    }

    moveImageToCurrentPosition() {
        const translationPercentage = 100 * this.currentIndex();
        this.slide.nativeElement.style.transform = `translateX(-${translationPercentage}%)`;
    }
}
