import { Routes } from '@angular/router';
import { CiHuiListComponent } from './subpage/ci-hui-list/ci-hui-list.component';
import { CiHuiLearnComponent } from './subpage/ci-hui-learn/ci-hui-learn.component';
import { CiHuiList1Component } from './subpage/ci-hui-list1/ci-hui-list1.component';

export const CI_HUI_ROUTES: Routes = [
  { path: '', component: CiHuiListComponent },
  { path: 'list1', component: CiHuiList1Component },
  { path: 'learn', component: CiHuiLearnComponent },
];

