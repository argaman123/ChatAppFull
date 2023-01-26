import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../services/chat.service";
import {LoginDataService} from "../../services/login-data.service";
import {ElectronService} from "../../services/electron.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers: { [email: string]: string } = {}
  registered: boolean = true
  messageHistory: ChatMessage[] = []

  constructor(private chat: ChatService, private loginData: LoginDataService,
              private electron: ElectronService) {
  }

  getNicknames() {
    return Object.values(this.activeUsers).sort()
  }

  getUsername(){
    return this.loginData.username
  }

  ngOnInit(): void {
    this.loginData.getLoginStatus().subscribe(status => {
      console.log("changed " + status)
      if (status) {
        this.chat.connect().subscribe(() => {
          this.chat.getMessageHistory().subscribe(messages => {
            this.messageHistory = messages
          })
          this.chat.getNewMessage().subscribe(message => {
            this.messageHistory.push(message)
            if (message.from != this.loginData.username)
              this.electron.ipcRenderer.send("notification", {title: "New message from " + message.from, body: message.content})
          })
          this.chat.getUsers().subscribe(allNicknames => {
            this.activeUsers = allNicknames
          })
          this.chat.getUserConnectionEvent().subscribe(event => {
            if (event.type == "connected")
              this.activeUsers[event.email] = event.nickname
            else if (event.type == "disconnected")
              delete this.activeUsers[event.email]
          })
        })
      }
    })
  }

  print(str: string) {
    this.chat.sendMessage(str)
    console.log(str)
  }

}
