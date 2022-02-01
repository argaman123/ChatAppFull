import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpStatusCode
} from "@angular/common/http";
import {catchError, map, Observable, of, throwError} from "rxjs";
import {AuthService} from "./auth.service";
import {LoginDataService} from "./login-data.service";

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(private loginData: LoginDataService) { }

  private handleAuthError(err: HttpErrorResponse): Observable<any> {
    if (err.status === 401 || err.status === 403) {
      //navigate /delete cookies or whatever
      this.loginData.immediateLogout()
      // if you've caught / handled the error, you don't want to rethrow it unless you also want downstream consumers to have to handle it as well.
      return of(err.message); // or EMPTY may be appropriate here
    }
    return throwError(() => err);
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Clone the request to add the new header.
    //const authReq = req.clone({headers: req.headers.set(Cookie.tokenKey, Cookie.getToken())});
    return next.handle(req).pipe(catchError(x=> this.handleAuthError(x))); //here use an arrow function, otherwise you may get "Cannot read property 'navigate' of undefined" on angular 4.4.2/net core 2/webpack 2.70
  }
}
