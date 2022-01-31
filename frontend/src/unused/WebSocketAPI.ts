import * as Stomp from 'stompjs'
import * as SockJS from 'sockjs-client'

export class WebSocketAPI {
  webSocketEndPoint: string = 'http://localhost:8080/chat-connection';
  topic: string = "/topic/chat";
  stompClient: any;
  constructor(){
    this._connect()
  }
  _connect() {
    console.log("Initialize WebSocket Connection");
    let ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    const _this = this;
    _this.stompClient.connect({}, () => {
      _this.stompClient.subscribe(_this.topic, (sdkEvent: any) => {
        _this.onMessageReceived(sdkEvent);
      });
      //_this.stompClient.reconnect_delay = 2000;
    }, this.errorCallBack);
    console.log(this.stompClient)
  };

  _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  // on error, schedule a reconnection attempt
  errorCallBack(error :string) {
    console.log("errorCallBack -> " + error)
    setTimeout(() => {
      this._connect();
    }, 5000);
  }

  /**
   * Send message to sever via web socket
   * @param {*} message
   */
  _send(message :ChatMessage) {
    console.log("calling logout api via web socket");
    this.stompClient.send("/app/message", {}, JSON.stringify(message));
  }

  onMessageReceived(message :string) {
    console.log("Message Recieved from Server :: " + message);
  }
}
