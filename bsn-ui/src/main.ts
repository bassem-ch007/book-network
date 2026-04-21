import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import {HttpClientModule, provideHttpClient, withInterceptors} from '@angular/common/http';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import {authInterceptor} from './app/services/interceptoToken/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule), // ✅ enables HttpClient everywhere
    provideHttpClient(withInterceptors([authInterceptor])) // ✅ register interceptor
  ]
}).catch(err => console.error(err));
