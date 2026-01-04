import { Component, inject } from '@angular/core';
import { SugarDictService } from '../../services/sugar-dict';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { AuthService } from '../../services/auth';

import { Router } from '@angular/router';
import { map } from 'rxjs';

interface CardItem {
  title: string;
  desc: string;
  href: string;
  learnedCount: number;
  sentencesCount: number;
  bgImage: string;
  progress?: string;
}

interface Section {
  name: string;
  items: CardItem[];
}

@Component({
  selector: 'app-user-summary',
  imports: [CommonModule, NzGridModule, NzProgressModule, NzToolTipModule, NzCardModule],
  templateUrl: './user-summary.component.html',
  styleUrl: './user-summary.component.css'
})
export class UserSummaryComponent {
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  sections: Section[] = [];

  ngOnInit(): void {
    this.sections.splice(0, 0, {name: '场景词汇', items: []});
    this.sections.splice(1, 0, {name: '口语表达', items: []});

    //get changjing cihui
    this.sugarDictService.getAllChildrenContentModules().pipe(
      map((response: any) => {
        const rawData = response.children || [];
        return rawData.map((item: any) => ({
          id: item.id,
          title: item.description,
          desc: item.description,
          learnedCount: 16,
          sentencesCount: item.wordsCount,
          bgImage: 'https://images.unsplash.com/photo-1501504905252-473c47e087f8?auto=format&fit=crop&w=800&q=80'
        } as any));
      })
    ).subscribe({
      next: (processedData: any[]) => {
        console.log(processedData);
        const cihui = {
          name: '场景词汇',
          items: processedData
        };
        this.sections.splice(0, 1, cihui);
      },
      error: (err) => console.error('请求失败:', err)
    });
    //get kouyu
    this.sugarDictService.getSentenceContentModules(this.currentUser()?.id || -1).pipe(
      map((response: any) => {
        const rawData = response.sentenceCases || [];
        return rawData.map((item: any) => ({
          id: item.id,
          title: item.description,
          desc: item.description,
          learnedCount: 16,
          sentencesCount: item.sentencesCount,
          bgImage: 'https://images.unsplash.com/photo-1501504905252-473c47e087f8?auto=format&fit=crop&w=800&q=80'
        } as any));
      })
    ).subscribe({
      next: (processedData: any[]) => {
        const kouyu = {
          name: '口语表达',
          items: processedData
        };
        this.sections.splice(1, 1, kouyu);
      },
      error: (err) => console.error('请求失败:', err)
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
