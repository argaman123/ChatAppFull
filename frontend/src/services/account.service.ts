import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {ChatService} from "./chat.service";
import {LoginDataService} from "./login-data.service";

const API = "http://localhost:8080/account/"
// merge login-data and account, they basically serve the same purpose
@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient, private chat: ChatService, private loginData: LoginDataService) { }

  changeNickname(nickname :string){
    return this.http.put(API + "nickname", nickname, {
      withCredentials: true,
      responseType: "text"
    })
  }

  changePassword(passwords : { oldPassword: string, newPassword: string }){
    return this.http.put(API + "password", passwords, {
      withCredentials: true,
      responseType: "text"
    })
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
