import { Component, inject } from '@angular/core';
import { SugarDictService } from '../../services/sugar-dict';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';

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

  sections: Section[] = [
    // {
    //   name: '场景词汇',
    //   items: [
    //     { title: '学习与学校', href: '/word/1', desc: '下午帮我签到', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80' },
    //     { title: '食物', href: '/word/1', desc: '来呗奶茶吧', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
    //     { title: '公共交通', href: '/word/1', desc: '地铁公交，一码通行', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
    //     { title: '住宅', href: '/word/1', desc: '满城尽带黄金甲', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=800&q=80' }
    //   ]
    // }
  ];

  ngOnInit(): void {
    this.sugarDictService.getAllContentModules().pipe(
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
        console.log('请求成功:', processedData);
        const kouyu = {
          name: '口语表达',
          items: processedData
        };
        this.sections.push(kouyu);
        console.log('Sections:', kouyu);
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
