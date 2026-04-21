import { Component } from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {Router} from '@angular/router';
import {CodeInputModule} from 'angular-code-input';
import {Confirm$Params} from '../../services/fn/authentication/confirm';
import {AuthenticationService} from '../../services/services/authentication.service';

@Component({
  selector: 'app-activate-account',
  imports: [
    NgIf,
    ReactiveFormsModule,
    CodeInputModule
  ],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {
  message= '';
  isOkay= true;
  submitted= false;

  constructor(private router:Router,
              private authenticationService:AuthenticationService) {
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }

  private confirmAccount(token: string) {
    this.authenticationService.confirm({token}).subscribe({
      next:()=>{
        this.submitted=true;
        this.isOkay=true
        this.message='your account has been successfully activated. \n know you can proceed to login';

      },
      error:()=>{
        this.message='invalid || expired  token';
        this.submitted=true;
        this.isOkay=false
      }
    })

  }
}
