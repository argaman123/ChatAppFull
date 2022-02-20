import {
  AfterViewChecked, AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {ChatService} from "../../services/chat.service";

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements AfterViewInit {
  @Input() messages!: ChatMessage[]
  @ViewChild('container') container : ElementRef | undefined;
  @ViewChildren('messages') messagesDiv: QueryList<ChatMessage> | undefined;

  currentMessage :ChatMessage | null = null

  ngAfterViewInit() {
    this.scrollToBottom();
    this.messagesDiv?.changes.subscribe(this.scrollToBottom);
  }

  scrollToBottom = () => {
    this.container!.nativeElement.scrollTop = this.container!.nativeElement.scrollHeight;
  }

  onSelect(message :ChatMessage){
    this.currentMessage = message
  }

  onUnselect(){
    this.currentMessage = null
  }

}
