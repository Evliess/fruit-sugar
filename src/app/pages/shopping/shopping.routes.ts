import { Routes } from '@angular/router';
import { ShoppingComponent } from './shopping.component';
import { WelcomeComponent } from '../welcome/welcome.component';

export const SHOPPING_ROUTES: Routes = [
  { path: '', component: ShoppingComponent },
  { path: ':id', component: WelcomeComponent },
  { path: '**', redirectTo: '' },
];
