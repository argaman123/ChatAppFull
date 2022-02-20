import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormGroupDirective, NgForm,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {AccountService} from "../../services/account.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ErrorStateMatcher} from "@angular/material/core";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {
  matcher = new class implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
      return form?.hasError("notSame") || false
    }
  }

  form: FormGroup = new FormGroup({
    oldPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
    newPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
    confirmedPassword: new FormControl('', [Validators.required])
  }, {
    validators: (group: AbstractControl): ValidationErrors | null => {
      return group.get("newPassword")?.value == group.get("confirmedPassword")?.value ? null : {notSame: true}
    }
  });

  constructor(public dialogRef: MatDialogRef<ChangePasswordComponent>,
              private accountService: AccountService,
              private snackBar: MatSnackBar,
  ) {
  }


  onOk() {
    if (this.form.valid) {
      this.accountService.changePassword(this.form.value).subscribe(
        {
          next: _ => {
            this.snackBar.open("Password was successfully changed", "ok", {
              duration: 3000,
            })
            this.dialogRef.close()
          },
          error: err => {
            this.snackBar.open(err.error, "try again", {
              duration: 3000,
            })
          }
        }
      )
    }
  }

  onCancel(): void {
    this.dialogRef.close()
  }

}
