import { Pipe, PipeTransform } from "@angular/core";

const offerStatusSet = new Map<string, string>();
offerStatusSet.set("PENDING", "Pendente");
offerStatusSet.set("ACCEPTED", "Aceito");
offerStatusSet.set("DENIED", "Negado");
offerStatusSet.set("CANCELLED", "Cancelado");

@Pipe({
    name: "translateOfferStatus",
})
export class TranslateOfferStatusPipe implements PipeTransform {
    transform(offerStatus: string | undefined | null): string | null {
        if (!offerStatus) {
            return null;
        }
        return offerStatusSet.get(offerStatus) || null;
    }
}
