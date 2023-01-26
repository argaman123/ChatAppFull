import {AfterViewInit, Component, ElementRef, Input, QueryList, ViewChild, ViewChildren} from '@angular/core';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements AfterViewInit {
  @Input() messages!: ChatMessage[]
  @Input() username!: string
  @ViewChild('container') container : ElementRef | undefined;
  @ViewChildren('messages') messagesDiv: QueryList<ChatMessage> | undefined;
  smoothScroll = false


  currentMessage :ChatMessage | null = null

  ngAfterViewInit() {
    this.messagesDiv?.changes.subscribe(this.scrollToBottom);
  }

  scrollToBottom = () => {
    this.container!.nativeElement.scrollTop = this.container!.nativeElement.scrollHeight;
    if (!this.smoothScroll)
      this.smoothScroll = true
  }

  onSelect(message :ChatMessage){
    this.currentMessage = message
  }

  onUnselect(){
    this.currentMessage = null
  }

}
