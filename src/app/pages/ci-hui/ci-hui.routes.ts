import { Routes } from '@angular/router';
import { CiHuiComponent } from './ci-hui.component';
import { CiHuiLearnComponent } from './ci-hui-learn.component';
import { CiHuiList1Component } from './ci-hui-list1.component';

export const CI_HUI_ROUTES: Routes = [
  { path: '', component: CiHuiComponent },
  { path: 'list1', component: CiHuiList1Component },
  { path: 'learn', component: CiHuiLearnComponent },
];

