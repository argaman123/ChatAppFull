import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainPageComponent} from "../views/main-page/main-page.component";
import {LoginPageComponent} from "../views/login-page/login-page.component";
import {AuthGuard} from "../unused/AuthGuard";
import {RegisterPageComponent} from "../views/register-page/register-page.component";
import {LoggedInGuard} from "../services/logged-in.guard";
import {LoggedOutGuard} from "../services/logged-out.guard";

const routes: Routes = [
  {path: '', component: MainPageComponent, canActivate: [LoggedInGuard]},//, canActivate: [AuthGuard]},
  {path: 'login', component: LoginPageComponent, canActivate: [LoggedOutGuard]},//, canActivate: [AuthGuard]}
  {path: 'register', component: RegisterPageComponent, canActivate: [LoggedOutGuard]},//, canActivate: [AuthGuard]}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
