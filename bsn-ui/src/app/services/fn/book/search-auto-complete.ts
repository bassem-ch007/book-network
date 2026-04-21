import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {StrictHttpResponse} from '../../strict-http-response';
import {RequestBuilder} from '../../request-builder';
import {BookDropdownResponse} from '../../models/book-dropdown-response';

export interface AutocompleteBooks$Params {
  query: string;
}

export function autocompleteBooks(http: HttpClient, rootUrl: string, params: AutocompleteBooks$Params, context?: HttpContext): Observable<StrictHttpResponse<BookDropdownResponse[]>> {
  const rb = new RequestBuilder(rootUrl, autocompleteBooks.PATH, 'get');
  rb.query('query', params.query, {});

  return http.request(rb.build({ responseType: 'json', accept: '*/*', context }))
    .pipe(
      filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => r as StrictHttpResponse<BookDropdownResponse[]>)
    );
}
autocompleteBooks.PATH = '/books/autocomplete';
