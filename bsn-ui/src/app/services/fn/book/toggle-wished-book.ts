import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CommonResponse} from '../../models/common-response';

export interface ToggleWishedBook$Params {
  'book-id': number;
}

export function toggleWishedBook(
  http: HttpClient,
  rootUrl: string,
  params: ToggleWishedBook$Params,
  context?: HttpContext
): Observable<StrictHttpResponse<CommonResponse<boolean>>> {
  const rb = new RequestBuilder(rootUrl, toggleWishedBook.PATH, 'post');
  if (params) {
    rb.path('book-id', params['book-id'], {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<CommonResponse<boolean>>;
    })
  );
}

toggleWishedBook.PATH = '/books/waiting-list/toggle/{book-id}';
