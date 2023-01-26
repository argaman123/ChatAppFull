import {Component, OnInit} from '@angular/core';
import {LoginDataService} from "../services/login-data.service";
import {MatDialog} from "@angular/material/dialog";
import {AuthenticationComponent} from "../modals/authentication/authentication.component";
import {OverlayContainer} from "@angular/cdk/overlay";

const authenticatedURLS = ["/"]

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Chat App';

  constructor(private loginData: LoginDataService, private dialog: MatDialog, private overlayContainer: OverlayContainer) {
    overlayContainer.getContainerElement().classList.add('darkMode');
  }

  ngOnInit(): void {
    this.loginData.getLoginStatus().subscribe(status => {
      if (!status)
        this.dialog.open(AuthenticationComponent)
    })
  }

}
