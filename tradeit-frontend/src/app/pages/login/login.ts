import { Component, inject } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserService } from "../../services/user-service";
import { Router, RouterLink } from "@angular/router";
import { InputWrapper } from "../shared/input-wrapper/input-wrapper";
import { FieldSet } from "../shared/field-set/field-set";
import { ButtonAndLink } from "../shared/button-and-link/button-and-link";

@Component({
    selector: "app-login",
    imports: [RouterLink, ReactiveFormsModule, InputWrapper, FieldSet, ButtonAndLink],
    templateUrl: "./login.html",
    styleUrl: "./login.css",
})
export class Login {
    private readonly userService = inject(UserService);
    private readonly router = inject(Router);
    protected readonly formGroup: FormGroup;

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            email: ["", Validators.required],
            password: ["", Validators.required],
        });
    }

    submitForm(): void {
        const email = this.formGroup.get("email")?.value as string;
        const password = this.formGroup.get("password")?.value as string;
        this.userService.authenticateUser(email, password).subscribe({
            next: () => this.redirectUserToAdvertisementsPage(),
            // TODO: handle possible errors here
        });
    }

    redirectUserToAdvertisementsPage() {
        this.router.navigate([""]);
    }
}
