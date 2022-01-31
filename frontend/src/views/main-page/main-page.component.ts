import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../services/chat.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers: User[] = [{nickname: "danny"}, {nickname: "bobby"}]
  registered: boolean = true
  messageHistory: ChatMessage[] = []

  constructor(private chat: ChatService) {
  }

  ngOnInit(): void {
    this.chat.connect().subscribe(() => {
      this.chat.getMessageHistory().subscribe(messages => {
        this.messageHistory = messages
      })
      this.chat.getNewMessage().subscribe(message => {
        this.messageHistory.push(message)
      })
    })
  }

  print(str: string) {
    this.chat.sendMessage(str)
    console.log(str)
  }

}
