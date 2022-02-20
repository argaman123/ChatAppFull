import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgForm,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {ErrorStateMatcher} from "@angular/material/core";

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss']
})
export class RegisterPageComponent {
  matcher = new class implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      return form?.hasError("notSame") || false
    }
  }
  errors: string = ''
  @Output() submit = new EventEmitter()
  form: FormGroup = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    nickname: new FormControl('', [
      Validators.required,
      Validators.maxLength(10),
      Validators.pattern(/[a-zA-Z]/),
      Validators.pattern(/^[\x00-\x7F]+$/) // ?
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
    confirmedPassword: new FormControl('', [Validators.required]),
    premiumPlan: new FormControl('none')
  }, {
    validators: (group: AbstractControl): ValidationErrors | null => {
      return group.get("password")?.value == group.get("confirmedPassword")?.value ? null : {notSame: true}
    }
  })

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
