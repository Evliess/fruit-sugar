import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/welcome' },
  { path: 'welcome', loadChildren: () => import('./pages/ci-hui/ci-hui.routes').then(m => m.CI_HUI_ROUTES) },
  { path: 'shopping', loadChildren: () => import('./pages/shopping/shopping.routes').then(m => m.SHOPPING_ROUTES) }
];
