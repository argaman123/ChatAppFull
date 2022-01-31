import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainPageComponent} from "../views/main-page/main-page.component";
import {LoginPageComponent} from "../views/login-page/login-page.component";
import {AuthGuard} from "../unused/AuthGuard";

const routes: Routes = [
  {path: '', component: MainPageComponent},//, canActivate: [AuthGuard]},
  {path: 'login', component: LoginPageComponent},//, canActivate: [AuthGuard]}
  {path: 'register', component: LoginPageComponent},//, canActivate: [AuthGuard]}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
