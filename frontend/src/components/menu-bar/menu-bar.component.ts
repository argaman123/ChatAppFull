import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrls: ['./menu-bar.component.scss']
})
export class MenuBarComponent {
  @Input() subscribed: boolean = false
  constructor(private authService: AuthService){}//, private router: Router) { }

  onSignOut(){
    this.authService.logout().subscribe(() => {
      //this.router.navigateByUrl("/login")
    })
  }

}
