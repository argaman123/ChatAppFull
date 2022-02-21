import {Component} from '@angular/core';
import {LoginDataService} from "../services/login-data.service";
import {MatDialog} from "@angular/material/dialog";
import {AuthenticationComponent} from "../modals/authentication/authentication.component";
import {NavigationEnd, Router, RouterEvent} from "@angular/router";
import {filter} from "rxjs";
import {OverlayContainer} from "@angular/cdk/overlay";

const authenticatedURLS = ["/"]

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Chat App';

  constructor(loginData: LoginDataService, private dialog: MatDialog, private router: Router, overlayContainer: OverlayContainer) {
    overlayContainer.getContainerElement().classList.add('darkMode');
    router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(event => {
      if (authenticatedURLS.includes((event as RouterEvent).url)) {
        loginData.getLoginStatus().subscribe(status => {
          if (!status)
            this.dialog.open(AuthenticationComponent)
        })
      }
    })
  }
}
