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
    return new Date(expiration) > new Date()
  }

  // One implementation for any time you need to immediately log out (used in authInterceptor for instance)
  immediateLogout(){
    this.clearLogin()
    this.router.navigateByUrl("/login")
  }

}
