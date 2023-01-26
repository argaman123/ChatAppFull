import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class LoginDataService {

  requestedURL = "/"
  username: string = ""

  saveApiURL(url: string | undefined){
    if (url != null)
      localStorage.setItem("api-url", url)
  }

  loadApiURL(suffix: string | null = null){
    return (localStorage.getItem("api-url") ?? "http://localhost:8080") + (suffix ?  "/" + suffix + "/" : "")
  }

  private _loginStatus = new BehaviorSubject<boolean>(this.isLoggedIn())

  constructor() {}

  /**
   * @return an Observable for a Boolean that represents whether the current user is logged in or not.
   */
  getLoginStatus(){
    return this._loginStatus.asObservable()
  }

  /**
   * @return the url that was requested before the user was redirected to the login page. Resets to "/" every time it's called.
   */
  getRequestedURL(){
    const url = this.requestedURL.toString()
    this.requestedURL = "/"
    return url
  }

  /**
   * @param expiration the expiration date of the current JWT. If the user is not logged in it will be undefined and the
   * function will notify getLoginStatus with the new status.
   */
  setLogin(expiration :string | null = null){
    if (expiration == null)
      localStorage.removeItem("expiration")
    else
      localStorage.setItem("expiration", expiration)
    if (this._loginStatus.value != this.isLoggedIn()) {
      this._loginStatus.next(!this._loginStatus.value)
    }
  }

  /**
   * @return true if the JWT hasn't expired yet, false otherwise.
   */
  isLoggedIn(){
    const expiration = localStorage.getItem("expiration")
    if (expiration == null) return false
    console.log(new Date(expiration).toLocaleString())
    console.log(new Date().toLocaleString())
    return new Date(expiration) > new Date()
  }

  /**
   * One implementation for any time you need to immediately log out, and clear all data.
   * Previously used in AuthInterceptor in order to quickly navigate to the login page but since it now only uses modals,
   * it isn't really needed other than just reloading the page.
   */
  immediateLogout(){
    this.setLogin()
    window.location.reload();
  }

}
