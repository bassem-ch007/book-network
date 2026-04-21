import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {RatingComponent} from '../../components/rating/rating.component';
import {PageResponseBorrowedBookResponse} from '../../../../services/models/page-response-borrowed-book-response';
import {BookService} from '../../../../services/services/book.service';
import {BorrowedBookResponse} from '../../../../services/models/borrowed-book-response';

@Component({
  selector: 'app-returned-books',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './returned-books.component.html',
  styleUrl: './returned-books.component.scss'
})
export class ReturnedBooksComponent implements OnInit{
  page:number=0;
  size:number=5;
  returnedBooksPage:PageResponseBorrowedBookResponse={};
  messages: Array<string>=[];
  messageLevel: string='';
  constructor(private bookService:BookService) {
  }
  ngOnInit(): void {
    this.findAllReturnedBooks();
  }
  goToFirstPage() {
    this.page=0;
    this.findAllReturnedBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllReturnedBooks();
  }

  goToPage(index: number) {
    this.page=index;
    this.findAllReturnedBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllReturnedBooks();
  }

  goToLastPage() {
    this.page=this.returnedBooksPage.totalPages as number - 1;
    this.findAllReturnedBooks();
  }

  isLastPage():boolean {
    return this.page==this.returnedBooksPage.totalPages as number -1 ;
  }

  private findAllReturnedBooks() {
    this.bookService.findAllReturnedBooks({page:this.page,size:this.size}).subscribe({
      next:(res)=>{this.returnedBooksPage=res;}
    });
  }

  approveBorrowedBook(BorrowedBook: BorrowedBookResponse) {
    this.bookService.approveBorrowedBook({'book-id': BorrowedBook.id as number}).subscribe({
      next:()=>{
        this.messageLevel='success';
        this.messages.push('Book return approved successfully.')
        setTimeout(()=>{
          this.messageLevel='';
          this.messages=[];
          this.findAllReturnedBooks();},3000);
        },
      error:(err)=>{
        console.log(BorrowedBook.id as number);
        this.messageLevel='error'
        if(err.error?.validationErrors){
          this.messages = err.error?.validationErrors;
        }
        else {
          this.messages.push(err.error.error);
        }
        setTimeout(()=>{
          this.messageLevel='';
          this.messages=[];
          this.findAllReturnedBooks();},3000);
      }
    })
  }
}
