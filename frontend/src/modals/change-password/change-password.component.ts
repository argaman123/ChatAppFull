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

class CustomErrorStateMatcher implements ErrorStateMatcher {
  errorCode: string
  constructor(errorCode: string) {
    this.errorCode = errorCode
  }

  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    return form?.hasError(this.errorCode) || false
  }
}

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent {
  matcherNew = new CustomErrorStateMatcher("notSameNew")
  matcherOld = new CustomErrorStateMatcher("notSameOld")

  form: FormGroup = new FormGroup({
    oldPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
    confirmedOldPassword: new FormControl('', [Validators.required]),
    newPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/\d/),
      Validators.pattern(/[a-zA-Z]/)
    ]),
    confirmedNewPassword: new FormControl('', [Validators.required])
  }, {
    validators: (group: AbstractControl): ValidationErrors | null => {
      const newSame = group.get("newPassword")?.value == group.get("confirmedNewPassword")?.value ? null : {notSameNew: true}
      const oldSame = group.get("oldPassword")?.value == group.get("confirmedOldPassword")?.value ? null : {notSameOld: true}
      return {...newSame, ...oldSame}
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
