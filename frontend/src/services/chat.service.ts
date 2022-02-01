import {Injectable} from '@angular/core';
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {HttpClient} from "@angular/common/http";
import {first, map, Observable, Subject} from "rxjs";
import {AuthService} from "./auth.service";
import {Message} from "stompjs";
import {Router} from "@angular/router";
import {LoginDataService} from "./login-data.service";

const API = "http://localhost:8080/"

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private stompClient: Stomp.Client | undefined

  constructor(private http: HttpClient, private loginData: LoginDataService) {
  }

  connect() {
    return new Observable(subscriber => {
      if (this.stompClient?.connected) {
        subscriber.next()
        return
      }
      try {
        let ws = new SockJS(API + "chat-connection");
        this.stompClient = Stomp.over(ws);
        this.stompClient.connect({}, () => {
          this._handleNewMessage()
          this._handleNewUser()
          subscriber.next()
        }, err => {
          this.loginData.immediateLogout()
          subscriber.error(err)
        })
      } catch (e) {
        this.loginData.immediateLogout()
        subscriber.error(e)
      }
    }).pipe(first())
  }

  disconnect(callback: (() => void)){
    this.stompClient?.disconnect(callback)
  }

  private _onNewMessage = new Subject<ChatMessage>()

  private _handleNewMessage() {
    const dateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}$/ // PROBABLY NEEDS A MORE ABSTRACT SOLUTION
    function reviver(key: string, value: any) {
      if (typeof value === "string" && dateFormat.test(value)) {
        return new Date(value);
      }
      return value;
    }

    this.stompClient?.subscribe("/topic/chat", (message: Message) => {
      console.log(message)
      this._onNewMessage.next(JSON.parse(message.body, reviver))
    })
  }

  getNewMessage() {
    return this._onNewMessage.asObservable()
  }

  getMessageHistory() {
    return this.http.get<ChatMessage[]>(API + "chat-history", {
      withCredentials: true,
    }).pipe(map(messages => {
      for (const message of messages)
        message.datetime = new Date(message.datetime)
      return messages
    }))
  }

  private _onNewUser = new Subject<String>()

  private _handleNewUser() {
    this.stompClient?.subscribe("/topic/users", () => {
      this._onNewUser.next("hi")
    })
  }

  getUsers() {
    return this._onNewUser.asObservable()
  }

  sendMessage(content: string) {
    console.log(this.stompClient)
    this.stompClient?.send("/app/send", {}, JSON.stringify({content}))
  }

}
