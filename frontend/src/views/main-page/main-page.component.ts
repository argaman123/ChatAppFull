import {Component, OnInit, Pipe, PipeTransform} from '@angular/core';
import {ChatService} from "../../services/chat.service";
import {BehaviorSubject, Subject} from "rxjs";
import {AccountService} from "../../services/account.service";
import {LoginDataService} from "../../services/login-data.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  activeUsers: { [email: string]: string } = {}
  registered: boolean = true
  messageHistory: ChatMessage[] = []
  notifyScroll = new Subject()
  premium: PremiumStatus = {plan: "none"}
  constructor(private chat: ChatService, private account: AccountService, private loginData: LoginDataService) {}

  getNicknames(){
    return Object.values(this.activeUsers).sort()
  }

  ngOnInit(): void {
    this.loginData.getLoginStatus().subscribe(status => {
      console.log("changed "+status)
      if (status){
        this.chat.connect().subscribe(() => {
          this.chat.getMessageHistory().subscribe(messages => {
            this.messageHistory = messages
            this.notifyScroll.next(null)
          })
          this.chat.getNewMessage().subscribe(message => {
            this.messageHistory.push(message)
            this.notifyScroll.next(null)
          })
          this.chat.getUsers().subscribe(allNicknames => {
            this.activeUsers = allNicknames as { [email: string]: string }
          })
          this.chat.getUserConnectionEvent().subscribe(event => {
            if (event.type == "connected")
              this.activeUsers[event.email] = event.nickname
            else if (event.type == "disconnected")
              delete this.activeUsers[event.email]
          })
          this.account.isPremium().subscribe(plan => {
            this.premium = plan
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
