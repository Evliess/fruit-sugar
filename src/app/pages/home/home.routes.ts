import { Routes } from "@angular/router";
import { HomeComponent } from "./home.component";
import { CiHuiComponent } from "../ci-hui/ci-hui.component";    

export const HOME_ROUTES: Routes = [
    { path: '', component: HomeComponent },
    { path: ':id', component: CiHuiComponent },
];