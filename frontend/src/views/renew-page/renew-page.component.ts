import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AccountService} from "../../services/account.service";

@Component({
  selector: 'app-renew-page',
  templateUrl: './renew-page.component.html',
  styleUrls: ['./renew-page.component.scss']
})
export class RenewPageComponent implements OnInit {

  constructor(private route: ActivatedRoute, private accountService: AccountService) { }
  status = "Trying to renew.."

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.accountService.renew(params['code']).subscribe({
        next: value => {
          this.status = value
          console.log(value)

        },
        error: err => {
          this.status = err.error
          console.log(err)

        }
      })
    })
  }

}
