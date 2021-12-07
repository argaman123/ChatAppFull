import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-message-area',
  templateUrl: './message-area.component.html',
  styleUrls: ['./message-area.component.scss']
})
export class MessageAreaComponent implements OnInit {

  @Output() send = new EventEmitter<string>()
  message :string = ""

  constructor() { }

  ngOnInit(): void {
  }

  onSend(){
    if (this.message) {
      this.send.emit(this.message)
      this.message = ""
    }
  }

}
