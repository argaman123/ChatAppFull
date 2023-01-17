import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoginDataService {
  constructor() {
  }

  requestedURL = "/"

  private _loginStatus = new BehaviorSubject<boolean>(this.isLoggedIn())

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
      console.log("pushed " + !this._loginStatus.value)
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
   * Saves the user type inside the LocalStorage.
   * @param type the type of user, either "user" or "guest", deletes it if the type is null.
   */
  setUserType(type :string | null = null){
    if (type == null)
      localStorage.removeItem("type")
    else
      localStorage.setItem("type", type)
  }

  /**
   * @return true if the currently connected account is a user, false otherwise (a guest).
   */
  isUser(){
    return localStorage.getItem("type") == "user"
  }

  /**
   * Deletes all the information that was saved in LocalStorage.
   */
  logout(){
    this.setLogin()
    this.setUserType()
  }

  /**
   * One implementation for any time you need to immediately log out, and clear all data.
   * Previously used in AuthInterceptor in order to quickly navigate to the login page but since it now only uses modals,
   * it isn't really needed other than just reloading the page.
   */
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
