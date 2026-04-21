import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink} from '@angular/router';
import {TokenService} from '../../../../services/token/token.service';
import {NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Subject} from 'rxjs';
import {BookService} from '../../../../services/services/book.service';
import {BookDropdownResponse} from '../../../../services/models/book-dropdown-response';

@Component({
  selector: 'app-menu',
  imports: [
    RouterLink,
    NgIf,
    FormsModule,
    NgForOf
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit{
  fullName: string = '';
  showSearchForm: boolean = false;
  query: string ='';
  dropdownVisible: boolean=true;
  suggestions:BookDropdownResponse[] =[];
  constructor(private tokenService:TokenService,private router: Router,private bookService:BookService) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const currentUrl = event.urlAfterRedirects;
        this.showSearchForm = currentUrl === '/books' || currentUrl.startsWith('/books/search');
      }
    });
  }
  ngOnInit(): void {
    const linkColor:NodeListOf<Element>=document.querySelectorAll('.nav-link');
    linkColor.forEach(link =>{
      if (window.location.href.endsWith(link.getAttribute('href')||'')){
        link.classList.add('active');
      }
      link.addEventListener('click',()=>{
        linkColor.forEach(l=>l.classList.remove('active'));
        link.classList.add('active');
      });
    });
    this.fullName=this.tokenService.getFullName();
    console.log(this.fullName);
  }



  onInputChange(): void {
    if (this.query.length <= 0) {
      this.suggestions = [];
      return;
    }
    this.bookService.autocompleteBooks({query:this.query}).subscribe({
      next: (res) => this.suggestions = res,
      error: (err) => console.error('Autocomplete error', err)
    });
  }


  hideDropdownWithDelay() {
    setTimeout(() => {
      this.dropdownVisible = false;
    }, 200); // 200ms delay gives time to click a suggestion
  }

  onSearchClick() {
    if (this.query.length>0) {
      this.dropdownVisible = false;
      this.router.navigate(['/books/search'], {queryParams: {query: this.query, page: 0, size: 10}});
    }
  }

  openBookDetail(id:number) {
    this.dropdownVisible = false;
    this.router.navigate(['books','book-details',id]);
  }
  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);//not mandatory
  }
}
