import { Injectable } from '@angular/core';
import {BehaviorSubject, first, map, Observable, Subject} from "rxjs";
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


  renew(email: string){
    return this.http.put(API + "renew", email, {
      responseType: 'text'
    })
  }

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

  private _premiumPlan = new BehaviorSubject<PremiumStatus>({plan: 'none'})

  isPremium(){
    this.http.get<PremiumStatus>(API + "premium", {
      withCredentials: true
    }).subscribe(plan => {
      plan.expiration = plan.expiration == null ? undefined : new Date(plan.expiration)
      this._premiumPlan.next(plan)
    })
    return this._premiumPlan.asObservable()
  }

  changePlan(plan :string){
    return this.http.put<PremiumStatus>(API + "premium", plan, {
      withCredentials: true
    }).pipe(map(newPlan => {
      newPlan.expiration = newPlan.expiration == null ? undefined : new Date(newPlan.expiration)
      this._premiumPlan.next(newPlan)
    }))
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
