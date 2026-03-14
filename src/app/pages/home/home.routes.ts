import { Routes } from "@angular/router";
import { HomeComponent } from "./home.component";
import { CiHuiListComponent } from "../ci-hui/subpage/ci-hui-list/ci-hui-list.component";

export const HOME_ROUTES: Routes = [
    { path: '', component: HomeComponent },
    { path: ':id', component: CiHuiListComponent },
];