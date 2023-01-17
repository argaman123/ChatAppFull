import {Injectable} from '@angular/core';
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {HttpClient} from "@angular/common/http";
import {first, map, Observable, Subject} from "rxjs";
import {AuthService} from "./auth.service";
import {Message} from "stompjs";
import {Router} from "@angular/router";
import {LoginDataService} from "./login-data.service";

const API = "http://localhost:8080/chat/"

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private stompClient: Stomp.Client | undefined

  constructor(private http: HttpClient, private loginData: LoginDataService) {
  }

  /**
   * Tries to connect to the web socket if not already connected, and then starts to listen to all the relevant topics
   * such as new messages, users that logged in, notifications etc.
   * @return a one time Observable the returns nothing if the connection was successful and an error with a reason otherwise.
   */
  connect() {
    return new Observable(subscriber => {
      if (this.stompClient?.connected) {
        subscriber.next()
        return
      }
      try {
        let ws = new SockJS(API + "connect");
        this.stompClient = Stomp.over(ws);
        this.stompClient.connect({}, () => {
          this._handleNewMessage()
          this._handleNewUser()
          this._handleMessageReply()
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

  /**
   * disconnects from the websocket and then runs the callback functions
   * @param callback a function that will be called after the disconnection process is done.
   */
  disconnect(callback: (() => void)) {
    this.stompClient?.disconnect(callback)
  }

  private _onNewMessage = new Subject<ChatMessage>()

  /**
   * A reviver function that is used to replace all date-like String values inside a Json to a real Date object.
   */
  private static _reviver(key: string, value: any) {
    const dateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}$/ // PROBABLY NEEDS A MORE ABSTRACT SOLUTION
    if (typeof value === "string" && dateFormat.test(value)) {
      return new Date(value);
    }
    return value;
  }

  /**
   * Notifies the getNewMessage Observable with a ChatMessage whenever the server replies to the last message sent by the user.
   */
  private _handleMessageReply() {
    this.stompClient?.subscribe("/user/topic/reply", (message: Message) => {
      console.log(message.body)
      this._onNewMessage.next(JSON.parse(message.body, ChatService._reviver))
    })
  }

  /**
   * Notifies the getNewMessage Observable with a ChatMessage whenever someone sends a message to the chat.
   */
  private _handleNewMessage() {
    this.stompClient?.subscribe("/topic/chat", (message: Message) => {
      console.log(message)
      this._onNewMessage.next(JSON.parse(message.body, ChatService._reviver))
    })
  }

  /**
   * @return an Observable for all incoming chat message and replies in the format of ChatMessage (who sent it, at what time,
   * the contents of the message, and is it a reply or a message)
   */
  getNewMessage() {
    return this._onNewMessage.asObservable()
  }

  /**
   * @return a one time Observable with all the messages that were ever sent in the chat, in a format of ChatMessage.
   */
  getMessageHistory() {
    return this.http.get<ChatMessage[]>(API + "history", {
      withCredentials: true,
    }).pipe(map(messages => {
      for (const message of messages)
        message.datetime = new Date(message.datetime)
      return messages
    }), first())
  }

  private _onNewUser = new Subject<UserConnectionEvent>()

  /**
   * Notifies the getUserConnectionEvent Observable with a UserConnectionEvent whenever someone joins or leaves the chat.
   */
  private _handleNewUser() {
    this.stompClient?.subscribe("/topic/users", event => {
      this._onNewUser.next(JSON.parse(event.body))
    })
  }

  /**
   * @return an Observable for everytime a user joins or leaves the chat, in the form of UserConnectionEvent, which contains
   * his email, nickname, and whether he joined or left.
   */
  getUserConnectionEvent() {
    return this._onNewUser.asObservable()
  }

  /**
   * @return a one time Observable with a list of all the users that are currently connected to the chat, in the following form:
   *
   * email: String -> nickname: String
   */
  getUsers() {
    return this.http.get<{[email :string]: string}>(API + "users", {
      withCredentials: true,
    }).pipe(first())
  }

  /**
   * Sends a chat message to the server.
   * @param content the text contents of the message
   */
  sendMessage(content: string) {
    console.log(this.stompClient)
    this.stompClient?.send("/chat/send", {}, JSON.stringify({content}))
  }

}
