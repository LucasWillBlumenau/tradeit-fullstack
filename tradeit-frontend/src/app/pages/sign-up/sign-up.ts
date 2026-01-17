import { Component, inject } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { UserService } from "../../services/user-service";
import { FieldSet } from "../shared/field-set/field-set";
import { InputWrapper } from "../shared/input-wrapper/input-wrapper";
import { ButtonAndLink } from "../shared/button-and-link/button-and-link";

@Component({
    selector: "app-sign-up",
    imports: [ReactiveFormsModule, RouterLink, FieldSet, InputWrapper, ButtonAndLink],
    templateUrl: "./sign-up.html",
    styleUrl: "./sign-up.css",
})
export class SignUp {
    protected readonly formGroup: FormGroup;
    private readonly userService = inject(UserService);
    private readonly router = inject(Router);

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            name: [""],
            email: [""],
            password: [""],
            passwordConfirmation: [""],
        });
    }

    submitForm(): void {
        const name = this.formGroup.get("name")?.value as string;
        const email = this.formGroup.get("email")?.value as string;
        const password = this.formGroup.get("password")?.value as string;

        this.userService.createUser(name, email, password).subscribe({
            next: () => this.router.navigate(["login"]),
            // TODO: add error handling
        });
    }
}
