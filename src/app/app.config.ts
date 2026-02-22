import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom, provideAppInitializer } from '@angular/core';
import { provideRouter, withDebugTracing } from '@angular/router';

import { routes } from './app.routes';
import { provideNzIcons } from 'ng-zorro-antd/icon';
import {
  MenuFoldOutline,
  MenuUnfoldOutline,
  DashboardOutline,
  UserOutline,
  SettingOutline,
  BellOutline,
  SearchOutline
} from '@ant-design/icons-angular/icons';
import { en_US, provideNzI18n } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { FormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './services/auth.interceptor';
import { AuthService } from './services/auth';

registerLocaleData(en);

const icons = [MenuFoldOutline, MenuUnfoldOutline, DashboardOutline, UserOutline, SettingOutline, BellOutline, SearchOutline];

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
  provideRouter(routes),
  // provideRouter(routes, withDebugTracing()),
  provideNzIcons(icons),
  provideNzI18n(en_US),
  importProvidersFrom(FormsModule),
  provideAnimationsAsync(),
  provideAppInitializer(() => {
    const authService = new AuthService();
    authService.loadSavedSession();
  }),
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  provideHttpClient(
    withInterceptorsFromDi()
  )
  ]
};
