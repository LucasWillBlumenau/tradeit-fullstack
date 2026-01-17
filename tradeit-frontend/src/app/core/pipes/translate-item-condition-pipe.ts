import { Pipe, PipeTransform } from "@angular/core";

const itemConditionsTranslations = new Map<string, string>();
itemConditionsTranslations.set("NEW", "Novo");
itemConditionsTranslations.set("NEARLY", "Semi-novo");
itemConditionsTranslations.set("USED", "Usado");

@Pipe({
    name: "translateItemCondition",
})
export class TranslateItemConditionPipe implements PipeTransform {
    transform(itemCondition: string | undefined | null): string | null {
        if (!itemCondition) {
            return null;
        }
        return itemConditionsTranslations.get(itemCondition) || null;
    }
}
