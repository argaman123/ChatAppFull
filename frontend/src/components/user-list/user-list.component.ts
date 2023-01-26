import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent {

  @Input() users!: string[]
  @Input() title!: string
  collapsed: boolean = false

  constructor() { }

  collapse(){
    this.collapsed = !this.collapsed;
  }

}
