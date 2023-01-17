import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractControl, Form, FormControl, FormGroup, FormGroupDirective, NgForm, ValidationErrors, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AuthService } from 'src/services/auth.service';
import { LoginDataService } from 'src/services/login-data.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent {

  currentForm = "guest"
  errors = ""
  matcher = new class implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      return form?.hasError("notSame") || false
    }
  }
  form = new FormGroup({})
  formControls: { [name: string]: FormControl } = {
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
  }
  currentControls: string[] = []

  constructor(private ref: MatDialogRef<AuthenticationComponent>, private authService: AuthService) {
    ref.disableClose = true
    this.switchForm(this.currentForm)
  }

  setControls(newControls: string[], options: any = null) {
    const controls :{[name: string]: FormControl} = {}
    for (const control of newControls) {
      controls[control] = this.formControls[control]
    }
    this.form = new FormGroup(controls, options || {})
  }

  switchForm(to: string) {
    this.currentForm = to
    this.errors = ""
    switch (this.currentForm) {
      case 'login':
        this.setControls(["email", "password"])
        break
      case 'register':
        this.setControls(["nickname", "email", "password", "confirmedPassword", "premiumPlan"], {
          validators: (group: AbstractControl): ValidationErrors | null => {
            if (group.get("password") != null && group.get("confirmedPassword") != null)
              return group.get("password")?.value == group.get("confirmedPassword")?.value ? null : { notSame: true }
            else
              return null
          }})
        break
      case 'guest':
        this.setControls(["nickname"])
        break
    }
  }

  onSubmit() {
    if (this.form.valid) {
      switch (this.currentForm) {
        case 'login':
          this.authService.login(this.form.value)
            .subscribe({
              next: () => {
                /*this.router.navigateByUrl(this.loginData.getRequestedURL())*/
                console.log("closed")
                this.ref.close()
              },
              error: err => { this.errors = err.error }
            })
          break;
        case 'register':
          this.authService.register(this.form.value)
            .subscribe({
              next: () => { this.switchForm("login") },
              error: err => { this.errors = err.error }
            })
          break;
        case 'guest':
          this.authService.guest(this.form.value)
            .subscribe({
              next: () => {
                /*this.router.navigateByUrl(this.loginData.getRequestedURL())*/
                this.ref.close()
                console.log("closed")
              },
              error: err => { this.errors = err.error }
            })
          break;
      }
    }
  }
}
