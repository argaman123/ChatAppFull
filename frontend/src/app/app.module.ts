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
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpHandler} from "@angular/common/http";
import {LoginPageComponent} from "../views/login-page/login-page.component";
import {AuthGuard} from "../unused/AuthGuard";
import {AuthService} from "../services/auth.service";
import {MatButtonModule} from "@angular/material/button";
import {ChatService} from "../services/chat.service";

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    UserListComponent,
    MessagesComponent,
    MessageAreaComponent,
    MenuBarComponent,
    LoginPageComponent
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
    MatButtonModule
  ],
  providers: [WebsocketService, AuthGuard, AuthService, ChatService],
  bootstrap: [AppComponent]
})
export class AppModule { }
