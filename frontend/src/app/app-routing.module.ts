import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainPageComponent} from "../views/main-page/main-page.component";
import {RenewPageComponent} from "../views/renew-page/renew-page.component";

const routes: Routes = [
  {path: '', component: MainPageComponent},// canActivate: [LoggedInGuard]},
  {path: 'renew/:code', component: RenewPageComponent},

  /*{path: 'login', component: LoginPageComponent},// canActivate: [LoggedOutGuard]},
  {path: 'register', component: RegisterPageComponent},// canActivate: [LoggedOutGuard]},
  {path: 'guest', component: GuestPageComponent,},// canActivate: [LoggedOutGuard]},
  {path: 'test', component: TestComponentComponent}*/
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
