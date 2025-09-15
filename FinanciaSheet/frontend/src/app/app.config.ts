import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { appRoutes } from './app.routes';
import { API_URL } from './app/api-url.token';
import { tokenInterceptor } from './core/token-interceptor';
import { errorInterceptor } from './core/error-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(appRoutes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withInterceptors([tokenInterceptor, errorInterceptor])),
    { provide: API_URL, useValue: 'http://localhost:8080/api/v1' },
  ],
};
