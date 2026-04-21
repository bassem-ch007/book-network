import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {BorrowedBookResponse} from '../../../../services/models/borrowed-book-response';
import {PageResponseBorrowedBookResponse} from '../../../../services/models/page-response-borrowed-book-response';
import {BookService} from '../../../../services/services/book.service';
import {FeedBackRequest} from '../../../../services/models/feed-back-request';
import {FormsModule} from '@angular/forms';
import {RatingComponent} from '../../components/rating/rating.component';
import {FeedbackService} from '../../../../services/services/feedback.service';

@Component({
  selector: 'app-borrowed-books',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    RatingComponent,
    NgClass
  ],
  templateUrl: './borrowed-books.component.html',
  styleUrl: './borrowed-books.component.scss'
})
export class BorrowedBooksComponent implements OnInit{
  private _size:number=5;
  private _page:number=0;
  borrowedBooks: PageResponseBorrowedBookResponse={};
  selectedBook:BorrowedBookResponse | undefined=undefined;
  feedBackRequest:FeedBackRequest={bookId: 0,note:0};
  messages: Array<string>=[];
  messageLevel: 'success' | 'error' | null = null;
  constructor(private bookService:BookService,
              private feedBackService:FeedbackService) {
  }
  get page(): number {
    return this._page;
  }

  set page(value: number) {
    this._page = value;
  }


  ngOnInit(): void {
    this.findBorrowedBooks();
  }

  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook=book;
    this.feedBackRequest.bookId = this.selectedBook?.id as number;
  }

  private findBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({page:this._page,size:this._size}).subscribe({
      next:(res)=>{
        this.borrowedBooks=res;
      }
    })
  }

  goToFirstPage() {
    this._page=0;
    this.findBorrowedBooks();
  }

  goToPreviousPage() {
    this._page--;
    this.findBorrowedBooks();
  }

  goToPage(index: number) {
    this._page=index;
    this.findBorrowedBooks();
  }

  goToNextPage() {
    this._page++;
    this.findBorrowedBooks();
  }

  goToLastPage() {
    this._page=this.borrowedBooks.totalPages as number - 1;
    this.findBorrowedBooks();
  }

  isLastPage():boolean {
    return this._page==this.borrowedBooks.totalPages as number -1 ;
  }

  returnBook(withFeedBack: boolean) {
    this.bookService.returnBorrowedBook({'book-id': this.selectedBook?.id as number}).subscribe({
      next: (res) => {
        this.messages = [];
        this.messageLevel = 'success';

        if (!withFeedBack) {
          this.messages.push('Book is returned successfully');
          this.messages.push('No feedback');
          setTimeout(() => {
            this.selectedBook = undefined;
            this.messages = [];
            this.messageLevel = null;
            this.findBorrowedBooks();
          }, 3000);
          return;
        }
        this.feedBackService.registerFeedback({ body: this.feedBackRequest }).subscribe({
          next: () => {
            this.messages.push('Book is returned successfully');
            this.messages.push('Feedback successfully registered');
            this.messageLevel = 'success';
            setTimeout(() => {
              this.selectedBook = undefined;
              this.messages = [];
              this.messageLevel = null;
              this.findBorrowedBooks();
            }, 3000);
          },
          error: (err) => {
            this.messages = err.error?.validationErrors || ['Failed to register the feedback'];
            this.messageLevel = 'error';
            setTimeout(() => {
              this.messages = [];
              this.messageLevel = null;
            }, 3000);
          }
        });
      },
      error: (err) => {
        this.messages = err.error?.validationErrors || ['Failed to return the book'];
        this.messageLevel = 'error';
        setTimeout(() => {
          this.messages = [];
          this.messageLevel = null;
        }, 3000);
      }
    });
  }

}
