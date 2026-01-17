import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, Observable, throwError } from "rxjs";

export const httpErrorHandlerInterceptor = (
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
    const router = inject(Router);
    return next(req).pipe(
        catchError((errResponse: HttpErrorResponse) => {
            performNeededRedirect(router, errResponse);
            return throwError(() => errResponse);
        }),
    );
};

const performNeededRedirect = (router: Router, errResponse: HttpErrorResponse) => {
    const resource = getErrorResource(errResponse.status);
    if (resource != null) {
        router.navigate([resource]);
    }
};

const getErrorResource = (status: number) => {
    if (status === 401) {
        return "login";
    }
    if (status === 403) {
        return "";
    }
    if (status >= 500 || status === 0) {
        return "error";
    }
    return null;
};
