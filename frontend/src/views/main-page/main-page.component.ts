import {Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import {ChatService} from "../../services/chat.service";
import {BehaviorSubject, Subject} from "rxjs";
import {AccountService} from "../../services/account.service";
import {LoginDataService} from "../../services/login-data.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers: { [email: string]: string } = {}
  registered: boolean = true
  messageHistory: ChatMessage[] = []

  constructor(private chat: ChatService, private account: AccountService, private loginData: LoginDataService,
              private snackBar: MatSnackBar) {
  }

  getNicknames() {
    return Object.values(this.activeUsers).sort()
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
