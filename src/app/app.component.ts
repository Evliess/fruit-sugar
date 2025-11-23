import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { CommonModule } from '@angular/common';

import { Router,} from '@angular/router';
import { CiHuiService } from './pages/ci-hui/ci-hui.service';


@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, NzIconModule, NzDropDownModule, NzLayoutModule, NzMenuModule, NzButtonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  isCollapsed = false;
  learnModel = "list";

  constructor(private router: Router) {}
  private showAllState = inject(CiHuiService)

  gotoWord() {this.router.navigate(['/word']);}
  gotoListen() {this.router.navigate(['/listen']);}
  setLearnModel(model: string) {
    this.learnModel = model;
    if(model=='learn') this.showAllState.setShowAll(true);
    if(model=='list') this.showAllState.setShowAll(false);
  }
}
