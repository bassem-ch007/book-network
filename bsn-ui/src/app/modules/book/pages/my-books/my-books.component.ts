import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {BookService} from '../../../../services/services/book.service';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
import {NgForOf} from '@angular/common';
import {BookCardComponent} from '../../components/book-card/book-card.component';
import {BookResponse} from '../../../../services/models/book-response';

@Component({
  selector: 'app-book-list',
  imports: [
    NgForOf,
    BookCardComponent,
    RouterLink,
  ],
  templateUrl: './my-books.component.html',
  styleUrl: './my-books.component.scss'
})
export class MyBooksComponent implements OnInit{
  page: number=0;
  size: number=8;
  bookResponse:PageResponseBookResponse={};
  successMessages = new Map<number, string>();
  errorMessages = new Map<number, string>();
  constructor(private router:Router,private bookService:BookService) {
  }

  ngOnInit(): void {
    this.findAllPersonalBooks();
  }

  private findAllPersonalBooks() {
    this.bookService.findAllPersonalBooks({
      page:this.page,
      size:this.size
    }).subscribe({
      next:(books)=>{
        this.bookResponse=books;
      }
    })
  }

  goToFirstPage() {
    this.page=0;
    this.findAllPersonalBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllPersonalBooks();
  }

  goToPage(index: number) {
    this.page=index;
    this.findAllPersonalBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllPersonalBooks();
  }

  goToLastPage() {
    this.page=this.bookResponse.totalPages as number - 1;
    this.findAllPersonalBooks();
  }

  isLastPage():boolean {
    return this.page==this.bookResponse.totalPages as number -1 ;
  }
  archiveBook(book: BookResponse) {
    this.errorMessages.clear();
    this.successMessages.clear();
    this.bookService.updateArchivedStatus({'book-id':book.id as number}).subscribe({
      next:()=>{
        this.successMessages.set(book.id!,'Updated archived status => '+!book.archived);
        this.errorMessages.delete(book.id!);
        setTimeout(()=>{
          this.successMessages.delete(book.id!);
          this.findAllPersonalBooks();
          },2000)
      },
      error:(err)=>{
        this.successMessages.clear();
        if (err.error.validationErrors) {
          this.errorMessages.set(book.id!, err.error.validationErrors.join(', '));
        } else {
          this.errorMessages.set(book.id!, err.error.error || 'Something went wrong');
        }
        setTimeout(() => {this.errorMessages.delete(book.id!);}, 2000);
      }
    })
  }

  shareBook(book: BookResponse) {
    this.errorMessages.clear();
    this.successMessages.clear();
    this.bookService.updateShareableStatus({'book-id':book.id as number}).subscribe({
      next:()=>{
        this.successMessages.set(book.id!,'Updated shared status => '+!book.shareable);
        this.errorMessages.delete(book.id!);
        setTimeout(()=>{
          this.successMessages.delete(book.id!);
          this.findAllPersonalBooks();
        },2000)
      },
      error:(err)=>{
        this.successMessages.clear();
        if (err.error.validationErrors) {
          this.errorMessages.set(book.id!, err.error.validationErrors.join(', '));
        } else {
          this.errorMessages.set(book.id!, err.error.error || 'Something went wrong');
        }
        setTimeout(() => {this.errorMessages.delete(book.id!);}, 2000);
      }
    })
  }

  editBook(book: BookResponse) {
    this.router.navigate(['books','my-books','book-manage',book.id]);
  }
}
