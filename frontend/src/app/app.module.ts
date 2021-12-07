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
import {FormsModule} from "@angular/forms";
import {WebSocketAPI} from "../services/WebSocketAPI";
import {WebsocketService} from "../services/websocket.service";
import {HttpHandler} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    UserListComponent,
    MessagesComponent,
    MessageAreaComponent,
    MenuBarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatListModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    FormsModule
  ],
  providers: [WebsocketService],
  bootstrap: [AppComponent]
})
export class AppModule { }
