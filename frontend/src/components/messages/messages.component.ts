import {AfterViewChecked, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ChatService} from "../../services/chat.service";

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements AfterViewChecked{
  @Input() messages!: ChatMessage[]
  @ViewChild('container') container : ElementRef | undefined;
  currentMessage :ChatMessage | null = null
  constructor(private chat: ChatService) {}

  ngAfterViewChecked(): void {
    try {
      this.container!.nativeElement.scrollTop = this.container!.nativeElement.scrollHeight;
    } catch(err) { }
  }

  onSelect(message :ChatMessage){
    this.currentMessage = message
  }

  onUnselect(){
    this.currentMessage = null
  }

}
