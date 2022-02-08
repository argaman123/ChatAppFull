import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {MainPageComponent} from "../views/main-page/main-page.component";
import {MatListModule} from "@angular/material/list";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from "@angular/material/icon";
import {UserListComponent} from "../components/user-list/user-list.component";
import {MessagesComponent} from "../components/messages/messages.component";
import {MatCardModule} from "@angular/material/card";
import {MessageAreaComponent} from "../components/message-area/message-area.component";
import {MenuBarComponent} from "../components/menu-bar/menu-bar.component";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {WebsocketService} from "../unused/websocket.service";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {LoginPageComponent} from "../views/login-page/login-page.component";
import {AuthGuard} from "../unused/AuthGuard";
import {AuthService} from "../services/auth.service";
import {MatButtonModule} from "@angular/material/button";
import {ChatService} from "../services/chat.service";
import {MatToolbarModule} from "@angular/material/toolbar";
import {RegisterPageComponent} from "../views/register-page/register-page.component";
import {MatMenuModule} from "@angular/material/menu";
import {AuthInterceptor} from "../services/auth.interceptor";
import {LoginDataService} from "../services/login-data.service";
import {GuestPageComponent} from "../views/guest-page/guest-page.component";
import {AccountService} from "../services/account.service";
import {ChangeNicknameComponent} from "../modals/change-nickname/change-nickname.component";
import {MatDialogModule} from "@angular/material/dialog";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {ChangePasswordComponent} from "../modals/change-password/change-password.component";

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    UserListComponent,
    MessagesComponent,
    MessageAreaComponent,
    MenuBarComponent,
    LoginPageComponent,
    RegisterPageComponent,
    GuestPageComponent,
    ChangeNicknameComponent,
    ChangePasswordComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatListModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatToolbarModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  providers: [WebsocketService, AuthGuard, AuthService, ChatService, LoginDataService, AccountService, {
    provide: HTTP_INTERCEPTORS,
    useFactory: function(loginData: LoginDataService) {
      return new AuthInterceptor(loginData);
    },
    multi: true,
    deps: [LoginDataService]
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
