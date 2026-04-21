import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BookResponse} from '../../../../services/models/book-response';
import {NgIf} from '@angular/common';
import {RatingComponent} from '../rating/rating.component';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [
    NgIf,
    RatingComponent,
  ],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss'
})
export class BookCardComponent {
  private _book:BookResponse={};
  private _bookCover: string | undefined;
  private _manage: boolean=false;
  @Input() successMsg?: string;
  @Input() errorMsg?: string;
  @Input() isWaitingList: boolean=false;

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }
  get book(): BookResponse {
    return this._book;
  }
  @Input()
  set bookCover(value:any){
    this._bookCover=value;
  }
  get bookCover(): string | undefined {
    if (this._book.cover){
      return 'data:image/jpg;base64,'+this._bookCover;
    }
    else return "https://i.etsystatic.com/16946144/r/il/f766e9/2185405359/il_570xN.2185405359_c7zq.jpg";
  }
  get manage(): boolean {
    return this._manage;
  }
  @Input()
  set manage(value: boolean) {
    this._manage = value;
  }
  @Output() private share:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();
  @Output() private archive:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();
  @Output() private addToWaitingList:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();
  @Output() private borrow:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();
  @Output() private edit:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();
  @Output() private details:EventEmitter<BookResponse>=new EventEmitter<BookResponse>();

  onShowDetails() {
    this.details.emit(this._book)
  }

  onAddToWaitingList() {
    this.addToWaitingList.emit(this._book)
  }

  onBorrow() {
    this.borrow.emit(this._book)
  }

  onEdit() {
    this.edit.emit(this._book)
  }

  onShare() {
    this.share.emit(this._book)
  }

  onArchive() {
    this.archive.emit(this._book)
  }
}
