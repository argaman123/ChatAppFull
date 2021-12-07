import { Component, OnInit } from '@angular/core';
import {WebSocketAPI} from "../../services/WebSocketAPI";
import {WebsocketService} from "../../services/websocket.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers :User[] = [{nickname: "danny"}, {nickname: "bobby"}]
  registered :boolean = true
  messageHistory :Message[] = []
  constructor(private webSocket: WebsocketService) {
    this.webSocket.messageHistoryObs.subscribe((message) => {
      this.messageHistory = message
    })
  }

  ngOnInit(): void {
  }

  print(str :string){
    const message = {datetime: new Date(), nickname: "User", content: str, email: "test@gmail.com"}
    this.webSocket.send(message)
    console.log(str)
  }

}
