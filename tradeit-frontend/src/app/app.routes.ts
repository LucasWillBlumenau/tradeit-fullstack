import { Routes } from "@angular/router";
import { Advertisements } from "./pages/home/advertisements/advertisements";
import { Login } from "./pages/login/login";
import { SignUp } from "./pages/sign-up/sign-up";
import { Home } from "./pages/home/home";
import { MyAdvertisements } from "./pages/home/my-advertisements/my-advertisements";
import { MyOffers } from "./pages/home/my-offers/my-offers";
import { Advertisement } from "./pages/home/advertisement/advertisement";
import { Categories } from "./pages/home/categories/categories";
import { Items } from "./pages/items/items";

export const routes: Routes = [
    {
        path: "",
        redirectTo: "/home/advertisements",
        pathMatch: "full",
    },
    {
        path: "home",
        component: Home,
        children: [
            {
                path: "advertisements",
                component: Advertisements,
            },
            {
                path: "advertisements/:id",
                component: Advertisement,
            },
            {
                path: "my-advertisements",
                component: MyAdvertisements,
            },
            {
                path: "my-offers",
                component: MyOffers,
            },
            {
                path: "categories",
                component: Categories,
            },
            {
                path: "items",
                component: Items,
            },
        ],
    },
    {
        path: "login",
        component: Login,
    },
    {
        path: "sign-up",
        component: SignUp,
    },
    {
        path: "error",
        component: Error,
    },
];
