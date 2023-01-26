import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {AuthService} from 'src/services/auth.service';
import {LoginDataService} from "../../services/login-data.service";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent {
  errors = ""

  form = new FormGroup({})
  formControls: { [name: string]: FormControl } = {
    nickname: new FormControl('', [
      Validators.required
    ]),
    password: new FormControl('', [
      Validators.required
    ]),
    server: new FormControl(this.loginData.loadApiURL(), [
      Validators.required
    ])
  }

  currentControls: string[] = []

  constructor(private ref: MatDialogRef<AuthenticationComponent>, private authService: AuthService, private loginData: LoginDataService) {
    ref.disableClose = true
    this.setControls(["nickname", "password", "server"])
  }

  setControls(newControls: string[], options: any = null) {
    const controls :{[name: string]: FormControl} = {}
    for (const control of newControls) {
      controls[control] = this.formControls[control]
    }
    this.form = new FormGroup(controls, options || {})
  }

  onSubmit() {
    if (this.form.valid) {
      this.loginData.saveApiURL(this.form.get("server")?.value)
      this.authService.login({nickname: this.form.get("nickname")?.value, password: this.form.get("password")?.value})
        .subscribe({
          next: () => {
            this.ref.close()
          },
          error: err => {
            this.errors = err.error
          }
        })
    }
  }
}
