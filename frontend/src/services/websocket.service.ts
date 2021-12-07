import {Injectable} from '@angular/core';
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {BehaviorSubject, Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private _webSocketEndPoint: string = 'http://localhost:8080/chat-connection';
  private _stompClient: any;
  private _messageHistory :BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>([])
  messageHistoryObs = this._messageHistory.asObservable()
  constructor(){
    this._connect()
  }
  _connect() {
    console.log("Initialize WebSocket Connection");
    let ws = new SockJS(this._webSocketEndPoint);
    this._stompClient = Stomp.over(ws);
    const _this = this;
    _this._stompClient.connect({}, () => {
      _this._stompClient.subscribe("/app/chat", (messages: any) => {
        _this.onMessageReceived(messages, true);
      });
      _this._stompClient.subscribe("/topic/chat", (message: any) => {
        _this.onMessageReceived(message);
      });
      //_this.stompClient.reconnect_delay = 2000;
    }, this._errorCallBack);
    console.log(this._stompClient)
  };

  _disconnect() {
    if (this._stompClient !== null) {
      this._stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  // on error, schedule a reconnection attempt
  _errorCallBack(error :string) {
    console.log("errorCallBack -> " + error)
    setTimeout(() => {
      this._connect();
    }, 5000);
  }

  /**
   * Send message to sever via web socket
   * @param {*} message
   */
  send(message :Message) {
    this._stompClient.send("/app/send", {}, JSON.stringify(message));
  }

  onMessageReceived(str :any, multiple = false) {
    const dateFormat = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}$/ // PROBABLY NEEDS A MORE ABSTRACT SOLUTION
    function reviver(key :string, value :any) {
      if (typeof value === "string" && dateFormat.test(value)) {
        return new Date(value);
      }
      return value;
    }
    if (multiple)
      this._messageHistory.next(JSON.parse(str.body, reviver))
    else {
      const temp = this._messageHistory.value
      temp.push(JSON.parse(str.body, reviver))
      this._messageHistory.next(temp)
    }
    console.log("Message Recieved from Server");
  }
}
