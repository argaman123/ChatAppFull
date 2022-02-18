import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainPageComponent} from "../views/main-page/main-page.component";
import {LoginPageComponent} from "../views/login-page/login-page.component";
import {AuthGuard} from "../unused/AuthGuard";
import {RegisterPageComponent} from "../views/register-page/register-page.component";
import {LoggedInGuard} from "../services/logged-in.guard";
import {LoggedOutGuard} from "../services/logged-out.guard";
import {GuestPageComponent} from "../views/guest-page/guest-page.component";
import {RenewPageComponent} from "../views/renew-page/renew-page.component";

const routes: Routes = [
  {path: '', component: MainPageComponent, canActivate: [LoggedInGuard]},//, canActivate: [AuthGuard]},
  {path: 'login', component: LoginPageComponent, canActivate: [LoggedOutGuard]},//, canActivate: [AuthGuard]}
  {path: 'register', component: RegisterPageComponent, canActivate: [LoggedOutGuard]},//, canActivate: [AuthGuard]}
  {path: 'guest', component: GuestPageComponent, canActivate: [LoggedOutGuard]},
  {path: 'renew/:code', component: RenewPageComponent}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
