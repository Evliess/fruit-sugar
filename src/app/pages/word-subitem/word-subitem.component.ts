import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { ActivatedRoute } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';


@Component({
  selector: 'app-word-subitem',
  imports: [CommonModule, NzGridModule, NzCardModule, NzButtonModule,NzProgressModule, NzToolTipModule],
  templateUrl: './word-subitem.component.html',
  styleUrl: './word-subitem.component.css'
})
export class WordSubitemComponent {
  wordSubitems: any[] = []
  isCustom: boolean = false;
  parentName: string = '';
  parentDescription: string = '';
  private router = inject(Router);
  constructor(private route: ActivatedRoute) { }
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  ngOnInit() {
    this.route.queryParamMap.subscribe(paramMap => {
      this.isCustom = paramMap.get('isCustom') === 'true';
      this.parentName = paramMap.get('parentName') || '';
      this.parentDescription = paramMap.get('parentDescription') || '';
      this.sugarDictService.getChildrenContentModules(this.parentName, this.currentUser()?.id || 0).subscribe((data: any) => {
        this.wordSubitems = data.children || [];
      });
    });

  }

  gotoWords(moduleId: number) {
    this.router.navigate(['/words'], { queryParams: { isCustom: this.isCustom, moduleId: moduleId } });
  }

  gotoResetWordsProgress(moduleId: number) {
    this.sugarDictService.resetWordsProgressByChildContentModuleId(moduleId, this.currentUser()?.id || 0).subscribe(() => {
      this.router.navigate(['/words'], { queryParams: { isCustom: this.isCustom, moduleId: moduleId } });
    });
  }

  getChineseContentModuleName(name: string) {
    return this.sugarDictService.getChineseContentModuleName(name);
  }
  getEnglishContentModuleName(name: string) {
    return this.sugarDictService.getEnglishContentModuleName(name);
  }

  getPercent(learnedCount: number, totalCount: number): number {
    if (totalCount === 0) return 0;
    return Math.round((learnedCount / totalCount) * 100);
  }

}
