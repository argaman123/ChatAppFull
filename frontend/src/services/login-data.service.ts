import {Injectable} from "@angular/core";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class LoginDataService {
  constructor(private router: Router) {
  }

  setLogin(expiration :string){
    localStorage.setItem("expiration", expiration)
  }

  clearLogin(){
    localStorage.removeItem("expiration")
  }

  isLoggedIn(){
    const expiration = localStorage.getItem("expiration")
    if (expiration == null) return false
    console.log(new Date(expiration).toLocaleString())
    console.log(new Date().toLocaleString())
    return new Date(expiration) > new Date()
  }

  // One implementation for any time you need to immediately log out (used in authInterceptor for instance)
  immediateLogout(){
    const url = this.router.routerState.snapshot.url
    if (!url.endsWith("/login") && !url.endsWith("/register") && !url.endsWith("guest")) {
      this.clearLogin()
      this.router.navigateByUrl("/login")
      return true
    }
    return false
  }

}
