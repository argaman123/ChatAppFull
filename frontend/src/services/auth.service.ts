import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {first, firstValueFrom, flatMap, map, Observable} from "rxjs";
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {ChatService} from "./chat.service";
import {Router} from "@angular/router";
import {LoginDataService} from "./login-data.service";

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
            this.loginData.setLogin(expiration)
            this.loginData.setUserType(type)
            this.chat.connect().subscribe({
              next: () => {
                subscriber.next()
              },
              error: err => {
                subscriber.error(err)
              }
            })
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

  login(credentials: { username: string, password: string }) {
    return this._loginLogic("user", credentials)
  }

  guest(credentials : { nickname :string }){
    return this._loginLogic("guest", credentials)
  }

  register(credentials: { email: string, nickname: string, password: string }) {
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
