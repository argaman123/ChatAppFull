import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../services/chat.service";
import {BehaviorSubject, Subject} from "rxjs";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers: User[] = [{nickname: "danny"}, {nickname: "bobby"}]
  registered: boolean = true
  messageHistory: ChatMessage[] = []
  notifyScroll = new Subject()

  constructor(private chat: ChatService) {
  }

  ngOnInit(): void {
    this.chat.connect().subscribe(() => {
      this.chat.getMessageHistory().subscribe(messages => {
        this.messageHistory = messages
        this.notifyScroll.next(null)
      })
      this.chat.getNewMessage().subscribe(message => {
        this.messageHistory.push(message)
        this.notifyScroll.next(null)
      })
      this.chat.getUsers().subscribe(text => {
        console.log(text)
      })
    })
  }

  print(str: string) {
    this.chat.sendMessage(str)
    console.log(str)
  }

}
