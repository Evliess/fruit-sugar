import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/home' },
  { path: 'home', loadChildren: () => import('./pages/home/home.routes').then(m => m.HOME_ROUTES) },
  { path: 'listen', loadChildren: () => import('./pages/listen-write/listen-write.routes').then(m => m.LISTEN_WRITE_ROUTES) },
  { path: 'word', loadChildren: () => import('./pages/ci-hui/ci-hui.routes').then(m => m.CI_HUI_ROUTES) }
];
