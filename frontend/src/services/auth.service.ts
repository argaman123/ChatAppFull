import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {first, Observable} from "rxjs";
import {ChatService} from "./chat.service";
import {LoginDataService} from "./login-data.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private chat: ChatService, private loginData: LoginDataService) {
  }

  /**
   * An abstract function that allows both users and guests to log in.
   * @param type the type of user, either "user" or "guest"
   * @param credentials the credentials that the user entered when trying to connect.
   * @return a one time Observable that returns an error with a reason if the login failed, and otherwise returns nothing
   * and proceeds with the login operation (saving the user type and JWT expiration in the LocalStorage)
   */
  private _loginLogic(type :string, credentials :any){
    return new Observable(subscriber => {
      try {
        this.http.post(this.loginData.loadApiURL("auth") + (type == "user" ? "login" : type), credentials, {
          responseType: 'text',
          withCredentials: true
        }).subscribe({
          next: expiration => {
            this.loginData.setLogin(expiration)
            subscriber.next()
          },
          error: err => {
            console.log(err)
            subscriber.error(err)
          }
        })
      } catch (e) {
        subscriber.error(e)
      }
    }).pipe(first())
  }

  /**
   * Tries to log in to a user account using an email address and a password.
   * @param credentials includes the email and password entered by the user. The password will be hashed before it gets
   * sent to the server, however I don't think it helps that much for security since the attacker probably doesn't even
   * need the real password. But I implemented it anyway since it was requested specifically.
   * @return a one time Observable that returns an error with a reason if the login failed, and otherwise returns nothing
   * and proceeds with the login operation (saving the user type and JWT expiration in the LocalStorage)
   */
  login(credentials: { nickname: string | undefined, password: string | undefined }) {
    return this._loginLogic("user", {nickname: credentials.nickname, password: credentials.password})
  }

  /** Tries to log out the currently logged-in user.
   * @return a one time Observable which returns an error with a reason if the operation failed, otherwise returns nothing
   * and proceeds to disconnect from the websocket and clear all the LocalStorage information that was saved during the "session".
   */
  logout() {
    return new Observable(subscriber => {
      this.http.get(this.loginData.loadApiURL("account") + "logout", {
        withCredentials: true
      }).subscribe({
          next: () => {
            this.loginData.immediateLogout()
            this.chat.disconnect(()=>{
              subscriber.next()
            })
          },
          error: err => {
            console.log(err)
            subscriber.error(err)
          }
        }
      )
    }).pipe(first())
  }

}
