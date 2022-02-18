import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthService} from "./auth.service";
import {LoginDataService} from "./login-data.service";

@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate {
  // here you can inject your auth service to check that user is signed in or not
  constructor(private loginData: LoginDataService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.loginData.isLoggedIn()) {
      this.loginData.requestedURL = state.url
      this.router.navigateByUrl("/login")
      return false;
    }
    return true;
  }
}
