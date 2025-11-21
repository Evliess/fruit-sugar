import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/welcome' },
  { path: 'welcome', loadChildren: () => import('./pages/listen-write/listen-write.routes').then(m => m.LISTEN_WRITE_ROUTES) },
  { path: 'shopping', loadChildren: () => import('./pages/shopping/shopping.routes').then(m => m.SHOPPING_ROUTES) }
];
