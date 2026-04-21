import {Component, OnInit} from '@angular/core';
import {AuthenticationRequest} from '../../services/models/authentication-request';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/services/authentication.service';
import {AuthenticationResponse} from '../../services/models/authentication-response';
import {NgForOf, NgIf} from '@angular/common';
import {TokenService} from '../../services/token/token.service';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  authRequest: AuthenticationRequest={email: '',password:''};
  errorMsg: Array<String>=[];
  constructor(private router:Router,
              private authService:AuthenticationService,
              private tokenService:TokenService) {}

  ngOnInit(): void {
    localStorage.removeItem('token');
    }

  login() {
    this.errorMsg=[];
    this.authService.authenticate({body:this.authRequest}).subscribe({
      next:(res:AuthenticationResponse)=>{
        this.tokenService.token= res.token as string;
        console.log(this.tokenService.token);
        this.router.navigate(['books']);
      },
      error:(err)=> {
        if (err.error.validationErrors)
        {
          console.log(err.error.validationErrors);
          this.errorMsg = err.error.validationErrors;
        }
        else
        {
          this.errorMsg.push(err.error.error);
        }
      }
    });
  }

  register() {
    this.router.navigate(['register']);
  }
}
