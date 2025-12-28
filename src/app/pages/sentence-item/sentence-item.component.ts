import { Component, inject } from '@angular/core';
import { SugarDictService } from '../../services/sugar-dict';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sentence-item',
  imports: [NzGridModule, NzCardModule, NzButtonModule],
  templateUrl: './sentence-item.component.html',
  styleUrl: './sentence-item.component.css'
})
export class SentenceItemComponent {
  sentenceCases: any[] = []
  private sugarDictService = inject(SugarDictService)

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.sugarDictService.getAllContentModules().subscribe({
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

  getChineseContentModuleName(name: string) {
    return this.sugarDictService.getChineseContentModuleName(name);
  }
  getEnglishContentModuleName(name: string) {
    return this.sugarDictService.getEnglishContentModuleName(name);
  }
}
