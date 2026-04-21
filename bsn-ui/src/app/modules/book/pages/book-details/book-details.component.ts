import {Component, OnInit} from '@angular/core';
import {BookRequest} from '../../../../services/models/book-request';
import {BookService} from '../../../../services/services/book.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-book-details',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './book-details.component.html',
  styleUrl: './book-details.component.scss'
})
export class BookDetailsComponent implements OnInit{
  errorMessage: Array<string> = [];
  bookId:number|undefined;
  private _bookCoverString: string =''
  bookRequest: BookRequest = {author: '', isbn: '', shareable: true, synopsis: '', title: '' };
  isWaitingList:boolean =false;
  constructor(private bookService: BookService,
              private router: Router,
              private activatedRoute:ActivatedRoute) {}

  ngOnInit(): void {
    this.bookId=this.activatedRoute.snapshot.params['book-id'];
    if (this.bookId){
      this.errorMessage=[];
      this.bookService.findById({'book-id':this.bookId}).subscribe({
          next:(bookResponse)=> {
            this.bookRequest = {
              id: this.bookId as number,
              author: bookResponse.author as string,
              isbn: bookResponse.isbn as string,
              shareable: bookResponse.shareable,
              synopsis: bookResponse.synopsis as string,
              title: bookResponse.title as string,
            };
            //this.bookCoverString = `data:${mime};base64,${rawCover}`;
            if (bookResponse.cover) {
              this._bookCoverString = `data:image/jpeg;base64,${bookResponse.cover}`;
            }
            this.bookService.toggleWishedBook({'book-id': this.bookId as number}).subscribe({
              next: (res) => {
                this.bookService.toggleWishedBook({'book-id': this.bookId as number}).subscribe({
                  next:(r)=>{
                    this.isWaitingList=r.data;
                  }
                })
              }
            });
          },
          error: (err) => {
            this.errorMessage.push(err.error.error);
            setTimeout(()=>{this.errorMessage=[];},2000);
          }
      });
    }
  }
  get bookCoverString(): string {
    return this._bookCoverString
  }

  returnBack() {
    this.router.navigate(['books']);
  }

  onAddToWaitingList() {
    this.errorMessage=[];
    this.errorMessage=[];
    this.bookService.toggleWishedBook({'book-id': this.bookId as number}).
    subscribe({
      next: (res) => {
        this.errorMessage.push('successfully toggled');
        setTimeout(() => {
            this.isWaitingList=res.data;
            this.errorMessage=[];
            this.bookService.findById({'book-id':this.bookId as number}).subscribe({
                next:(bookResponse)=>{
                  this.bookRequest={
                    id: this.bookId as number,
                    author:bookResponse.author as string,
                    isbn:bookResponse.isbn as string,
                    shareable:bookResponse.shareable,
                    synopsis:bookResponse.synopsis as string,
                    title:bookResponse.title as string,};
                  //this.bookCoverString = `data:${mime};base64,${rawCover}`;
                  if (bookResponse.cover) {
                    this._bookCoverString = `data:image/jpeg;base64,${bookResponse.cover}`;
                  }
                }});
          },
          1500);
      },
      error: (err) => {
        this.errorMessage=[];
        if (err.error.validationErrors) {
          this.errorMessage.push(""+err.error.validationErrors.join(', '));
        } else {
          this.errorMessage.push(""+err.error.error || 'Something went wrong');
        }
        setTimeout(() => {this.errorMessage=[];}, 2000);
      }
    });

  }

}
