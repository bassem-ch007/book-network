import {Component, OnInit} from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { BookRequest } from '../../../../services/models/book-request';
import { BookService } from '../../../../services/services/book.service';
import {BookResponse} from '../../../../services/models/book-response';

@Component({
  selector: 'app-manage-book',
  imports: [NgIf, NgForOf, FormsModule, RouterLink],
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss'] // fixed typo: styleUrl -> styleUrls
})
export class ManageBookComponent implements OnInit{
  errorMessage: Array<string> = [];
  bookCoverFile: File | null = null;
  private _bookCoverString: string =''
  bookRequest: BookRequest = {author: '', isbn: '', shareable: true, synopsis: '', title: '' };
  constructor(private bookService: BookService,
              private router: Router,
              private activatedRoute:ActivatedRoute) {}

  ngOnInit(): void {
    const bookId=this.activatedRoute.snapshot.params['book-id'];
    if (bookId){
      this.errorMessage=[];
      this.bookService.findById({'book-id':bookId}).subscribe({
        next:(bookResponse)=>{
          this.bookRequest={
            id: bookId as number,
            author:bookResponse.author as string,
            isbn:bookResponse.isbn as string,
            shareable:bookResponse.shareable,
            synopsis:bookResponse.synopsis as string,
            title:bookResponse.title as string,};
          //this.bookCoverString = `data:${mime};base64,${rawCover}`;
          if (bookResponse.cover) {
            this._bookCoverString = `data:image/jpeg;base64,${bookResponse.cover}`;
          }
          console.log(this._bookCoverString)
        },
        error: (err) => {
          this.errorMessage.push(err.error.error);
          setTimeout(()=>{this.errorMessage=[];},3000);
        }
        }
      )
    }
    }

  get bookCoverString(): string {
    return this._bookCoverString
  }

  set bookCoverString(value: string) {
    this._bookCoverString = value;
  }

  onFileSelected(event: any) {
    this.bookCoverFile = event.target.files[0];
    console.log(this.bookCoverFile);
    if (!this.bookCoverFile) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.bookCoverString = reader.result as string;
      console.log(this.bookCoverString);
    };
    reader.readAsDataURL(this.bookCoverFile);

  }

  saveBook() {
    this.bookService.registerBook({ body: this.bookRequest }).subscribe({
      next: (res) => {
        if (!this.bookCoverFile) {
          console.log('No cover file selected, skipping upload.');
          this.errorMessage.push('Book is saved successfully')
          this.errorMessage.push('No cover file selected, skipping upload.')
          setTimeout(()=>{this.router.navigate(['/books/my-books']);},2000)
          return;
        }

        const formData = new FormData();
        formData.append('file', this.bookCoverFile); // must be a File object

        this.bookService.uploadBookCoverPicture({
          'book-id': res.data,
          body: formData
        }).subscribe({
          next: () => {
            console.log('Cover uploaded successfully');
            this.router.navigate(['/books/my-books']);
          },
          error: (err) => {
            console.error('Error uploading cover:', err);
            this.errorMessage = err.error?.validationErrors || ['Failed to upload cover'];
            setTimeout(() => this.errorMessage = [], 3500);
          }
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.validationErrors || ['Failed to register book'];
        setTimeout(() => this.errorMessage = [], 3500);
      }
    });
  }
}
