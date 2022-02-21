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

  // The idea of hashing the password before sending is pretty much useless, since the attacker probably doesn't even
  // need the real password. But I implemented it anyway since it was requested

  login(credentials: { email: string, password: string }) {
    return this._loginLogic("user", {username: credentials.email, password: Hash.sha256(credentials.password)})
  }

  guest(credentials : { nickname :string }){
    return this._loginLogic("guest", credentials)
  }

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
