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

  login(credentials: { username: string, password: string }) {
    return new Observable(subscriber => {
      try {
        this.http.post(API + "login", credentials, {
          responseType: 'text',
          withCredentials: true
        }).subscribe({
          next: expiration => {
            this.loginData.setLogin(expiration)
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

  guest(credentials : { nickname :string }){
    return new Observable(subscriber => {
      try {
        this.http.post(API + "guest", credentials, {
          responseType: 'text',
          withCredentials: true
        }).subscribe({
          next: expiration => {
            this.loginData.setLogin(expiration)
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

  logout() {
    return new Observable(subscriber => {
      this.http.get(API + "logout", {
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
    })
  }
}
