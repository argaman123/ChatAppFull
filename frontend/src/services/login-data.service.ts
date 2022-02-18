import {Injectable} from "@angular/core";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class LoginDataService {
  constructor(private router: Router) {
  }

  requestedURL = "/"

  getRequestedURL(){
    const url = this.requestedURL.toString()
    this.requestedURL = "/"
    return url
  }

  setLogin(expiration :string | null = null){
    if (expiration == null)
      localStorage.removeItem("expiration")
    else
      localStorage.setItem("expiration", expiration)
  }

  isLoggedIn(){
    const expiration = localStorage.getItem("expiration")
    if (expiration == null) return false
    console.log(new Date(expiration).toLocaleString())
    console.log(new Date().toLocaleString())
    return new Date(expiration) > new Date()
  }

  setUserType(type :string | null = null){
    if (type == null)
      localStorage.removeItem("type")
    else
      localStorage.setItem("type", type)
  }

  isUser(){
    return localStorage.getItem("type") == "user"
  }

  logout(){
    this.setLogin()
    this.setUserType()
  }

  // One implementation for any time you need to immediately log out (used in authInterceptor for instance)
  immediateLogout(){
    const url = this.router.routerState.snapshot.url
    if (!url.endsWith("/login") && !url.endsWith("/register") && !url.endsWith("guest")) {
      this.logout()
      this.router.navigateByUrl("/login")
      return true
    }
    return false
  }

}
