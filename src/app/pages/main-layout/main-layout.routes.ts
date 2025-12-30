import { Routes } from '@angular/router';

export const MAIN_LAYOUT_ROUTES: Routes = [
  { path: '', loadChildren: () => import('./main-layout.routes').then(m => m.MAIN_LAYOUT_ROUTES) }
]; 