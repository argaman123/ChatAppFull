import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../services/account.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-change-nickname',
  templateUrl: './change-nickname.component.html',
  styleUrls: ['./change-nickname.component.scss']
})
export class ChangeNicknameComponent {
  form: FormGroup = new FormGroup({
    nickname: new FormControl('', [
      Validators.required,
      Validators.maxLength(10),
      Validators.pattern(/[a-zA-Z]/),
      Validators.pattern(/^[\x00-\x7F]+$/) // ?
    ]),
  });

  constructor(public dialogRef: MatDialogRef<ChangeNicknameComponent>,
              private accountService: AccountService,
              private snackBar: MatSnackBar,) {}

  onOk() {
    if (this.form.valid) {
      this.accountService.changeNickname(this.form.value.nickname).subscribe(
        {
          next: _ => {
            this.snackBar.open("Success! Your new nickname will be used after reloading the page", "reload", {
              duration: 3000,
            }).onAction().subscribe(() => {
              window.location.reload();
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
