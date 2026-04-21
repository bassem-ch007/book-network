import { Injectable } from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private jwtHelper = new JwtHelperService();

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token(): string | null {
    return localStorage.getItem('token');
  }

  isTokenNotValid(): boolean {
    return !this.isTokenValid();
  }

  private isTokenValid(): boolean {
    const token = this.token;
    if (!token) return false;

    const isExpired = this.jwtHelper.isTokenExpired(token);

    if (isExpired) {
      localStorage.removeItem('token'); // only remove the token
      return false;
    }

    return true;
  }
  decodeToken(): any {
    return this.token ? this.jwtHelper.decodeToken(this.token) : null;
  }
  getFullName(): string {
    const token = this.token;
    if (!token) return '';
    const decoded: any =this.decodeToken();
    const fullName= decoded?.['full name'] || ''; // access the custom claim
    return fullName.split(' ')[0] || '';
  }
}
