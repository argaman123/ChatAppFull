import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {

  error: string | null = null
  @Output() submit = new EventEmitter()
  form: FormGroup = new FormGroup({
    username: new FormControl('',[
      Validators.required,
      Validators.email,
    ]),
    password: new FormControl('',[
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
    ]),
  });

  constructor(private authService: AuthService, private router: Router) {
  }

  onSubmit() {
    if (this.form.valid) {
      this.authService.login(this.form.value)
        .subscribe({
          next: () => {
            this.router.navigateByUrl("/")
            /*.then(
              nav => {
                console.log(nav); // true if navigation is successful
              },
              err => {
                console.log(err) // when there's an error
              });*/
          },
          error: err => {
            this.error = err.error
          }
        })
    }
  }

}
