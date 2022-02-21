import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoginDataService {
  constructor(private router: Router) {
  }

  requestedURL = "/"

  private _loginStatus = new BehaviorSubject<boolean>(this.isLoggedIn())

  getLoginStatus(){
    return this._loginStatus.asObservable()
  }

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
    if (this._loginStatus.value != this.isLoggedIn()) {
      console.log("pushed " + !this._loginStatus.value)
      this._loginStatus.next(!this._loginStatus.value)
    }
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
    /*const url = this.router.routerState.snapshot.url
    if (!url.endsWith("/login") && !url.endsWith("/register") && !url.endsWith("/guest")) {
      this.logout()
      this.router.navigateByUrl("/login")
      return true
    }
    return false
*/
    this.logout()
    window.location.reload(); // only to clear all the data inside all components
  }

}
