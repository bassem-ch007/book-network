import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {StrictHttpResponse} from '../../strict-http-response';
import {RequestBuilder} from '../../request-builder';
import {PageResponseBookResponse} from '../../models/page-response-book-response';

export interface MyWaitingList$Params {
  page?: number;
  size?: number;
}

export function myWaitingList(
  http: HttpClient,
  rootUrl: string,
  params?: MyWaitingList$Params,
  context?: HttpContext
): Observable<StrictHttpResponse<PageResponseBookResponse>> {
  const rb = new RequestBuilder(rootUrl, myWaitingList.PATH, 'get');
  if (params) {
    rb.query('page', params.page ?? 0, {});
    rb.query('size', params.size ?? 10, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PageResponseBookResponse>;
    })
  );
}

myWaitingList.PATH = '/books/my-waiting-list';
