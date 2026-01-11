import { Component, inject } from '@angular/core';
import { SugarDictService } from '../../services/sugar-dict';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { AuthService } from '../../services/auth';

interface CardItem {
  title?: string;
  desc?: string;
  count?: number;
  total?: number;
  className?: string;
}

interface Section {
  name: string;
  items: CardItem[];
}

@Component({
  selector: 'app-user-summary',
  imports: [CommonModule, NzGridModule, NzCardModule],
  templateUrl: './user-summary.component.html',
  styleUrl: './user-summary.component.css'
})
export class UserSummaryComponent {
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  sections: Section[] = [];

  ngOnInit(): void {
    this.sections.splice(0, 0, { name: '学习概览', items: [] });

    this.sugarDictService.getUserStatistic(this.currentUser()?.id || -1).subscribe({
      next: (response: any) => {
        // 处理响应数据
        const mistakeWords = response.mistakeWords || 0;
        const mistakeSentences = response.mistakeSentences || 0;
        const learnedWords = response.learnedWords || 0;
        const learnedSentences = response.learnedSentences || 0;
        const unknownWords = response.unknownWords || 0;
        const unknownSentences = response.unknownSentences || 0;
        const customWords = response.customWords || 0;
        const customSentences = response.customSentences || 0;
        const totalWords = learnedWords + unknownWords + customWords;
        const totalSentences = learnedSentences + unknownSentences + customSentences;
        const totalMistakes = mistakeWords + mistakeSentences;
        
        this.sections[0].items.push({
          title: '已掌握词汇',
          count: learnedWords,
          total: totalWords,
          className: 'learned'
        });

        this.sections[0].items.push({
          title: '已掌握句子总数',
          count: learnedSentences,
          total: totalSentences,
          className: 'learned'
        });

        this.sections[0].items.push({
          title: '累计错误词汇',
          count: mistakeWords,
          total: totalMistakes,
          className: 'mistake'
        });

        this.sections[0].items.push({
          title: '累计错误句子',
          count: mistakeSentences,
          total: totalMistakes,
          className: 'mistake'
        });

        this.sections[0].items.push({
          title: '不认识词汇总数',
          count: unknownWords,
          className: 'unknown'
        });
        this.sections[0].items.push({
          title: '不认识句子',
          count: unknownSentences,
          className: 'unknown'
        });
        this.sections[0].items.push({
          title: '自定义词汇总数',
          count: customWords,
          className: 'custom'
        });
        this.sections[0].items.push({
          title: '自定义句子总数',
          count: customSentences,
          className: 'custom'
        });
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

}
