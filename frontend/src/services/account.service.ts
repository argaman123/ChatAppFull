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
   * Used when trying to renew directly from the URL that was sent to the user.
   * @param email a unique code that was received at the end of the URL.
   * @return A one time Observable for a String that contains the result of the operation.
   */
  renew(email: string){
    return this.http.put(API + "renew", email, {
      responseType: 'text'
    }).pipe(first())
  }

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

  private _premiumPlan = new BehaviorSubject<PremiumStatus>({plan: 'none'})

  /**
   * @return an Observable that returns the current PremiumStatus of the user, which contains his current plan and the
   * expiration date of that plan (if any), every time it changes.
   */
  getPremiumStatus(){
    this.http.get<PremiumStatus>(API + "premium", {
      withCredentials: true
    }).subscribe(plan => {
      plan.expiration = plan.expiration == null ? undefined : new Date(plan.expiration)
      this._premiumPlan.next(plan)
    })
    return this._premiumPlan.asObservable()
  }

  /**
   * Allows the user to change his current premium plan.
   * @param plan the new plan the user wishes to switch to.
   * @return a one time Observable for the new PremiumStatus of the user after the operation was successful, and an error
   * otherwise. Also notifies the getPremiumStatus Observable with the same value.
   */
  changePremiumPlan(plan :string){
    return this.http.put<PremiumStatus>(API + "premium", plan, {
      withCredentials: true
    }).pipe(map(newPlan => {
      newPlan.expiration = newPlan.expiration == null ? undefined : new Date(newPlan.expiration)
      this._premiumPlan.next(newPlan)
    }), first())
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
