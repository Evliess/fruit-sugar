import { Component, inject } from '@angular/core';
import { SugarDictService } from '../../services/sugar-dict';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-sentence-item',
  imports: [NzGridModule, NzCardModule, NzButtonModule, NzProgressModule, NzToolTipModule],
  templateUrl: './sentence-item.component.html',
  styleUrl: './sentence-item.component.css'
})
export class SentenceItemComponent {
  sentenceCases: any[] = []
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.sugarDictService.getSentenceContentModules(this.currentUser()?.id || -1).subscribe({
      next: (response: any) => {
        this.sentenceCases = response.sentenceCases;
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

  gotoKouYu(isCustom: boolean = false, moduleId: string) {
    if (!isCustom) this.router.navigate(['/kou-yu'], { queryParams: { isCustom: false, moduleId: moduleId } });
    else this.router.navigate(['/kou-yu'], { queryParams: { isCustom: true, moduleId: moduleId } });
  }

  resetSentencesProgressByModuleId(contentModuleId: number) {
    this.sugarDictService.resetSentencesProgressByModuleId(contentModuleId, this.currentUser()?.id || -1).subscribe({
      next: (response: any) => {
        this.router.navigate(['/kou-yu'], { queryParams: { isCustom: false, moduleId: contentModuleId } });
      },
      error: (err) => {
        console.error('重置例句进度失败:', err);
      }
    })
  };

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
