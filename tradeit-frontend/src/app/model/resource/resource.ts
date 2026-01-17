export type ResourceType = "video" | "image";

export interface Resource {
    type: ResourceType;
    url: string;
}
