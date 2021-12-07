import {AfterViewChecked, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit, AfterViewChecked{
  @Input() messages!: Message[]
  @ViewChild('container') container : ElementRef | undefined;
  constructor() { }

  ngAfterViewChecked(): void {
    try {
      this.container!.nativeElement.scrollTop = this.container!.nativeElement.scrollHeight;
    } catch(err) { }
  }
  ngOnInit(): void {
  }

}
