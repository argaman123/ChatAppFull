import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss']
})
export class RegisterPageComponent {

  error: string | null = null
  errors: string = ''
  @Output() submit = new EventEmitter()
  form: FormGroup = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    nickname: new FormControl('', [
      Validators.required,
      Validators.maxLength(20)
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
  });

  constructor(private authService: AuthService, private router: Router) {
  }

  onSubmit() {
    if(this.form.valid){
      this.authService.register(this.form.value).subscribe({
        next: () => {
          this.router.navigateByUrl("/login")
        },
        error: err => {
          this.errors = err
        }
      })
    }
  }

}
