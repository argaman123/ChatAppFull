import {Injectable} from '@angular/core';
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {HttpClient} from "@angular/common/http";
import {first, map, Observable, Subject} from "rxjs";
import {AuthService} from "./auth.service";
import {Message} from "stompjs";

const API = "http://localhost:8080/"

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  stompClient: Stomp.Client | undefined

  constructor(private http: HttpClient) {
  }

  connect() {
    return new Observable(subscriber => {
      if (this.stompClient) {
        subscriber.next()
        return
      }
      let ws = new SockJS(API + "chat-connection");
      this.stompClient = Stomp.over(ws);
      this.stompClient.connect({}, () => {
        subscriber.next()
      }, err => {
        subscriber.error(err)
      })
    }).pipe(first())
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

  onNewMessage = new Observable<ChatMessage>()

  getNewMessage() {
    const dateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}$/ // PROBABLY NEEDS A MORE ABSTRACT SOLUTION
    function reviver(key: string, value: any) {
      if (typeof value === "string" && dateFormat.test(value)) {
        return new Date(value);
      }
      return value;
    }
    return new Observable<ChatMessage>(subscriber => {
      this.stompClient?.subscribe("/topic/chat", (message: Message) => {
        console.log(message)
        subscriber.next(JSON.parse(message.body, reviver))
      })
    })
  }

  sendMessage(content: string) {
    console.log(this.stompClient)
    this.stompClient?.send("/app/send", {}, JSON.stringify({content}))
  }

}
