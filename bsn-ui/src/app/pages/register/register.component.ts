import {Component} from '@angular/core';
import {RegistrationRequest} from '../../services/models/registration-request';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/services/authentication.service';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerRequest: RegistrationRequest={email: '',firstName:'',lastName: '',password: ''};
  errorMsg: Array<String>=[];
  constructor(private router:Router,
              private authService:AuthenticationService) {}

  login() {
    this.router.navigate(['login']);
  }

  register() {
    this.errorMsg = [];
    this.authService.registerUser({
      body: this.registerRequest
    })
      .subscribe({
        next: (res) => {
          console.log(res);
          this.router.navigate(['activate-account']);
        },
        error: (err) => {
          if (err.error.validationErrors){
            console.log(err.error.validationErrors);
            this.errorMsg = err.error.validationErrors;}
          else this.errorMsg.push(err.error.error);
        }
      });
  }
}
