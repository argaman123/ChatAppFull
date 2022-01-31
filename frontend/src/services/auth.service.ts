import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {first, firstValueFrom, flatMap, map, Observable} from "rxjs";
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {ChatService} from "./chat.service";

const API = "http://localhost:8080/"

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private chat: ChatService) {
  }

  login(credentials: { username: string, password: string }) {
    return new Observable(subscriber => {
      this.http.post(API + "authenticate", credentials, {
        responseType: 'text',
        withCredentials: true
      }).subscribe({
        next: expiration => {
          localStorage.setItem("expiration", expiration)
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
    }).pipe(first())
  }

  loggedIn() {
    const expiration = localStorage.getItem("expiration")
    if (expiration == null) return false
    return new Date(expiration) > new Date()
  }
}
