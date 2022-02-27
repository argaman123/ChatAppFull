import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {first, firstValueFrom, map, Observable} from "rxjs";
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {ChatService} from "./chat.service";
import {Router} from "@angular/router";
import {LoginDataService} from "./login-data.service";
import * as Hash from "js-sha256"
const API = "http://localhost:8080/auth/"

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
        this.http.post(API + (type == "user" ? "login" : type), credentials, {
          responseType: 'text',
          withCredentials: true
        }).subscribe({
          next: expiration => {
            this.loginData.setUserType(type)
            this.loginData.setLogin(expiration)
            subscriber.next()
            /*this.chat.connect().subscribe({
              next: () => {
                subscriber.next()
              },
              error: err => {
                subscriber.error(err)
              }
            })*/
          },
          error: err => {
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
  login(credentials: { email: string, password: string }) {
    return this._loginLogic("user", {username: credentials.email, password: Hash.sha256(credentials.password)})
  }

  /**
   * Tries to log in to a guest account using just a nickname.
   * @param credentials includes the nickname that the user chose.
   * @return a one time Observable that returns an error with a reason if the login failed, and otherwise returns nothing
   * and proceeds with the login operation (saving the user type and JWT expiration in the LocalStorage)
   */
  guest(credentials : { nickname :string }){
    return this._loginLogic("guest", credentials)
  }

  /**
   * Tries to register a new account using just a nickname, password and email entered by the user.
   * @param credentials includes the nickname, password and email that the user chose.
   * @return a one time Observable that returns an error with a reason if the register failed, and otherwise returns nothing.
   */
  register(credentials: { email: string, nickname: string, password: string }) {
    credentials.password = Hash.sha256(credentials.password)
    return new Observable<string>(subscriber => {
      this.http.post(API + "register", credentials, {
        responseType: 'text',
        withCredentials: true
      }).subscribe({
        next: _ => {
          console.log(_)
          subscriber.next()
        },
        error: err => {
          subscriber.error(err.error)
        }
      })
    }).pipe(first())
  }

}
