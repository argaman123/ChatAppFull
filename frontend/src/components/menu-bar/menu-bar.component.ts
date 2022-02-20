import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {LoginDataService} from "../../services/login-data.service";
import {AccountService} from "../../services/account.service";
import {MatDialog} from "@angular/material/dialog";
import {ChangeNicknameComponent} from "../../modals/change-nickname/change-nickname.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ChangePasswordComponent} from "../../modals/change-password/change-password.component";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrls: ['./menu-bar.component.scss']
})
export class MenuBarComponent {
  @Input() premium!: PremiumStatus

  constructor(private accountService: AccountService,
              private loginData: LoginDataService,
              public dialog: MatDialog,
              private snackBar: MatSnackBar) {

  }

  isUser() {
    return this.loginData.isUser()
  }

  onSignOut() {
    this.accountService.logout().subscribe(() => {
    })
  }

  onChangeNickname() {
    this.dialog.open(ChangeNicknameComponent)
  }

  onChangePassword() {
    this.dialog.open(ChangePasswordComponent)
  }

  onPlan(plan: string) {
    this.accountService.changePlan(plan).subscribe(() => {
      console.log(plan)
      // Reloading the page will allow the backend to refresh ChatUser premium plan
      // TODO: Fix (?) Exploit: Opening the chat in multiple windows will allow you to still have the current plan perks
      window.location.reload()
    })
  }

}
