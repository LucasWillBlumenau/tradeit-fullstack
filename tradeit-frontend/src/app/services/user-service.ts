import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: "root",
})
export class UserService {
    private readonly httpClient = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    authenticateUser(email: string, password: string): Observable<void> {
        const body = { email, password };
        return this.httpClient.post<void>(`${this.apiUrl}/api/v1/auth/login`, body, { withCredentials: true });
    }

    createUser(name: string, email: string, password: string): Observable<void> {
        const body = { name, email, password };
        return this.httpClient.post<void>(`${this.apiUrl}/api/v1/auth/signup`, body);
    }
}
