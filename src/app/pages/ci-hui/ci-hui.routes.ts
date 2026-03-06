import { Routes } from '@angular/router';
import { CiHuiComponent } from './ci-hui.component';
import { CiHuiLearnComponent } from './ci-hui-learn.component';

export const CI_HUI_ROUTES: Routes = [
  { path: '', component: CiHuiComponent },
  { path: 'learn', component: CiHuiLearnComponent },
];

