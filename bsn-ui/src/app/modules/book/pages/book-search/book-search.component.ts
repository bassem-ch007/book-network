import {Component, OnInit} from '@angular/core';
import {BookCardComponent} from "../../components/book-card/book-card.component";
import {NgForOf} from "@angular/common";
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../../../../services/services/book.service';
import {BookResponse} from '../../../../services/models/book-response';

@Component({
  selector: 'app-book-search',
    imports: [
        BookCardComponent,
        NgForOf
    ],
  templateUrl: './book-search.component.html',
  styleUrl: './book-search.component.scss'
})
export class BookSearchComponent implements OnInit{
  query:string='';
  page: number=0;
  size: number=8;
  bookResponse:PageResponseBookResponse={};
  successMessages = new Map<number, string>();
  errorMessages = new Map<number, string>();
  waitingListIds: Set<number> = new Set<number>();
  constructor(private route:ActivatedRoute, private router:Router,private bookService:BookService) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.query = params['query'] || '';
      this.page = +params['page'] || 0;
      this.size = +params['size'] || 10;

      // Run search after queryParams are updated
      this.bookService.searchBooks({
        query: this.query,
        page: this.page,
        size: this.size
      }).subscribe({
        next: (books) => {
          this.bookResponse = books;

          this.bookService.myWaitingList({
            page: this.page,
            size: this.size
          }).subscribe({
            next: (books) => {
              this.waitingListIds = new Set<number>(books.content?.map(b => b.id!) ?? []);
            }
          });
        }
      });
    });
  }


  private findAllBooks() {
    this.bookService.searchBooks({
      query:this.query,
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
    this.findAllBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  goToPage(index: number) {
    this.page=index;
    this.findAllBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBooks();
  }

  goToLastPage() {
    this.page=this.bookResponse.totalPages as number - 1;
    this.findAllBooks();
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
      next: (res) => {
        this.successMessages.set(book.id!,'successfully toggled');
        this.errorMessages.delete(book.id!);
        setTimeout(() => {
            this.successMessages.delete(book.id!);
            this.bookService.searchBooks({
              query:this.query,
              page:this.page,
              size:this.size
            }).subscribe({
              next:(books)=>{
                this.bookResponse=books;
                this.bookService.myWaitingList({
                  page:this.page,
                  size:this.size
                }).subscribe({
                  next:(books)=>{
                    this.waitingListIds = new Set<number>(books.content?.map(b => b.id!) ?? []);}
                })
              }
            })}
          ,
          2000);
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

