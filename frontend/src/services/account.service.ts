import { Injectable } from '@angular/core';
import {BehaviorSubject, first, map, Observable, Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {ChatService} from "./chat.service";
import {LoginDataService} from "./login-data.service";
import * as Hash from "js-sha256";

const API = "http://localhost:8080/account/"
// merge login-data and account, they basically serve the same purpose
@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient, private chat: ChatService, private loginData: LoginDataService) { }

  /**
   * Allows the user to propose a new nickname for his account.
   * @param nickname the new nickname that the user wishes to be called from now on.
   * @return A one time Observable for a String that contains the result of the operation, however, any result that isn't
   * an error counts as a success.
   */
  changeNickname(nickname :string){
    return this.http.put(API + "nickname", nickname, {
      withCredentials: true,
      responseType: "text"
    }).pipe(first())
  }

  /**
   * Allows the user to change his current password.
   * @param passwords an Object that contains the current and the new password of the user.
   * @return A one time Observable for a String that contains the result of the operation, however, any result that isn't
   * an error counts as a success.
   */
  changePassword(passwords : { oldPassword: string, newPassword: string }){
    return this.http.put(API + "password", {oldPassword: Hash.sha256(passwords.oldPassword), newPassword: Hash.sha256(passwords.newPassword)}, {
      withCredentials: true,
      responseType: "text"
    }).pipe(first())
  }

  /** Tries to log out the currently logged-in user.
   * @return a one time Observable which returns an error with a reason if the operation failed, otherwise returns nothing
   * and proceeds to disconnect from the websocket and clear all the LocalStorage information that was saved during the "session".
   */
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
    }).pipe(first())
  }

}
