import {Component, Input, OnInit} from '@angular/core';
import {BookService} from '../../../../services/services/book.service';
import {BookCardComponent} from '../../components/book-card/book-card.component';
import {NgForOf} from '@angular/common';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
import {Router} from '@angular/router';
import {BookResponse} from '../../../../services/models/book-response';

@Component({
  selector: 'app-waiting-list',
  imports: [
    BookCardComponent,
    NgForOf
  ],
  templateUrl: './waiting-list.component.html',
  styleUrl: './waiting-list.component.scss'
})
export class WaitingListComponent implements OnInit{
  page: number=0;
  size: number=8;
  bookResponse:PageResponseBookResponse={};
  successMessages = new Map<number, string>();
  errorMessages = new Map<number, string>();
  waitingListIds: Set<number> = new Set<number>();
  constructor(private router:Router,private bookService:BookService) {
  }
  ngOnInit(): void {
    this.bookService.myWaitingList({
      page:this.page,
      size:this.size
    }).subscribe({
      next:(books)=>{
        this.bookResponse=books;
        this.waitingListIds = new Set<number>(books.content?.map(b => b.id!) ?? []);
      }
    })
  }

  private findWaitingList() {
    this.bookService.myWaitingList({
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
    this.findWaitingList();
  }

  goToPreviousPage() {
    this.page--;
    this.findWaitingList();
  }

  goToPage(index: number) {
    this.page=index;
    this.findWaitingList();
  }

  goToNextPage() {
    this.page++;
    this.findWaitingList();
  }

  goToLastPage() {
    this.page=this.bookResponse.totalPages as number - 1;
    this.findWaitingList();
  }

  isLastPage():boolean {
    return this.page==this.bookResponse.totalPages as number -1 ;
  }

  borrowBook(book: BookResponse) {
    this.errorMessages.clear();
    this.successMessages.clear();
    this.bookService.borrowBook({'book-id': book.id as number}).
    subscribe({
      next: () => {
        this.successMessages.set(book.id!,'Book successfully borrowed');
        this.errorMessages.delete(book.id!);
        setTimeout(() => {this.successMessages.delete(book.id!);}, 2000);
      },
      error: (err) => {
        this.successMessages.delete(book.id!);
        if (err.error.validationErrors) {
          this.errorMessages.set(book.id!, err.error.validationErrors.join(', '));
        } else {
          this.errorMessages.set(book.id!, err.error.error || 'Something went wrong');
        }
        setTimeout(() => {this.errorMessages.delete(book.id!);}, 2000);
      }
    });
  }

  toggleWishedBook(book: BookResponse) {
    this.errorMessages.clear();
    this.successMessages.clear();
    this.bookService.toggleWishedBook({'book-id': book.id as number}).
    subscribe({
      next: () => {
        this.successMessages.set(book.id!,'successfully toggled');
        this.errorMessages.delete(book.id!);
        setTimeout(() => {
          this.successMessages.delete(book.id!);
          this.findWaitingList();
          }, 2000);
      },
      error: (err) => {
        this.successMessages.delete(book.id!);
        if (err.error.validationErrors) {
          this.errorMessages.set(book.id!, err.error.validationErrors.join(', '));
        } else {
          this.errorMessages.set(book.id!, err.error.error || 'Something went wrong');
        }
        setTimeout(() => {this.errorMessages.delete(book.id!);}, 2000);
      }
    });
  }

  showBookDetails(book: BookResponse) {
    this.router.navigate(['books','book-details',book.id]);
  }

  isWished(book: BookResponse) {
    return this.waitingListIds.has(book.id!);
  }
}
