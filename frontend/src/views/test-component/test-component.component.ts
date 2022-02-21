import { AuthenticationComponent } from 'src/modals/authentication/authentication.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-test-component',
  templateUrl: './test-component.component.html',
  styleUrls: ['./test-component.component.scss']
})
export class TestComponentComponent implements OnInit {

  constructor(public dialog: MatDialog,) { }

  ngOnInit() {
    this.dialog.open(AuthenticationComponent, { disableClose: true })
  }

}
