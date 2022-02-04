import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-guest-page',
  templateUrl: './guest-page.component.html',
  styleUrls: ['./guest-page.component.scss']
})
export class GuestPageComponent{

  error: string | null = null
  @Output() submit = new EventEmitter()
  form: FormGroup = new FormGroup({
    nickname: new FormControl('',[
      Validators.required,
    ]),
  });

  constructor(private authService: AuthService, private router: Router) {
  }

  onSubmit() {
    if (this.form.valid) {
      this.authService.guest(this.form.value)
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
