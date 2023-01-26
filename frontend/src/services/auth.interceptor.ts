import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, Observable, of, throwError} from "rxjs";
import {LoginDataService} from "./login-data.service";

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(private loginData: LoginDataService) { }

  /**
   * If during the connection process to the web socket, the server returned an error, logout imminently.
   * It is mainly used because sometimes I would close the server, and I would want the frontend to reload.
   */
  private handleAuthError(err: HttpErrorResponse): Observable<any> {
    // TODO : make it more abstract
    if ((err.status === 401 || err.status === 403) && err.url?.includes("/chat/connect")) {
      this.loginData.immediateLogout()
      return of(err.message); // not sure
    }
    return throwError(() => err);
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(catchError(x=> this.handleAuthError(x)));
  }
}
