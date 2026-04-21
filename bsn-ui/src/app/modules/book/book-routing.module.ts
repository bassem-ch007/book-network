import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from './pages/main/main.component';
import {BookListComponent} from './pages/book-list/book-list.component';
import {MyBooksComponent} from './pages/my-books/my-books.component';
import {ManageBookComponent} from './pages/manage-book/manage-book.component';
import {BorrowedBooksComponent} from './pages/borrowed-books/borrowed-books.component';
import {ReturnedBooksComponent} from './pages/returned-books/returned-books.component';
import {authGuard} from '../../services/guard/auth.guard';
import {WaitingListComponent} from './pages/waiting-list/waiting-list.component';
import {BookDetailsComponent} from './pages/book-details/book-details.component';
import {BookSearchComponent} from './pages/book-search/book-search.component';

const routes: Routes = [
  {
    path: '',
    component:MainComponent,
    canActivate:[authGuard],
    children:[
      {
        path:'',
        component:BookListComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-books',
        component:MyBooksComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-books/book-manage',
        component:ManageBookComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-books/book-manage/:book-id',
        component:ManageBookComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-borrowed-books',
        component:BorrowedBooksComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-returned-books',
        component:ReturnedBooksComponent,
        canActivate:[authGuard]
      },
      {
        path:'my-waiting-list',
        component:WaitingListComponent,
        canActivate:[authGuard]
      },
      {
        path:'book-details/:book-id',
        component:BookDetailsComponent,
        canActivate:[authGuard]
      },
      {
        path:'search',
        component:BookSearchComponent,
        canActivate:[authGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookRoutingModule { }
