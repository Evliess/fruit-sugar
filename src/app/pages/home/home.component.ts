import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { forkJoin } from 'rxjs';

import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, NzGridModule, NzCardModule, NzButtonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor(private router: Router) { }
  sentenceCases: any[] = []
  wordCases: any[] = []
  private sugarDictService = inject(SugarDictService)
  sections: any[] = [];
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;
  wrongBookCount = 0;
  unknownBookCount = 0;

  ngOnInit(): void {
    const userId = this.currentUser()?.id || -1;

    forkJoin({
      contentModules: this.sugarDictService.getSentenceContentModules(userId),
      userStats: this.sugarDictService.getUserStatistic(userId)
    }).subscribe({
      next: ({ contentModules, userStats }: any) => {
        this.sentenceCases = contentModules.sentenceCases;
        this.wordCases = contentModules.wordCases;

        // 设置导航卡片计数
        const mistakeWords = userStats.mistakeWords || 0;
        const mistakeSentences = userStats.mistakeSentences || 0;
        const unknownWords = userStats.unknownWords || 0;
        const unknownSentences = userStats.unknownSentences || 0;
        this.wrongBookCount = mistakeWords + mistakeSentences;
        this.unknownBookCount = unknownWords + unknownSentences;

        this.sections = [];

        this.sections.push({
          name: '场景词汇',
          items: this.wordCases
        });
        // this.sections.push({
        //   name: '去听写',
        //   items: this.wordCases
        // });
        this.sections.push({
          name: '口语表达',
          items: this.sentenceCases
        });

        // AI助教部分 - 静态数据
        this.sections.push({
          name: 'AI助教',
          items: [
            { id: 'ai-word', name: 'AI词汇', description: '智能词汇学习助手' },
            { id: 'ai-sentence', name: 'AI句子', description: '智能句子学习助手' }
          ]
        });

        // 学习管理部分 - 错题本和生词本
        this.sections.push({
          name: '学习管理',
          items: [
            { id: 'wrong-book', name: '错题本', description: '错题记录', count: this.wrongBookCount },
            { id: 'unknown-book', name: '生词本', description: '学习进度', count: this.unknownBookCount }
          ]
        });
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

  gotoWordSubitem(type: string, parentId: number, parentName: string, parentDescription: string, isCustom: boolean = false) {
    this.router.navigate(['/word-subitem'],
      { queryParams: { type: type, isCustom: isCustom, parentId: parentId, parentName: parentName, parentDescription: parentDescription } });
  }
  gotoListenWrite(id: number) {
    console.log("gotoListenWrite");
    this.router.navigate(['/listen', id]);
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

  navigateToAiWord(): void {
    this.router.navigate(['/ai-learn/ai-word']);
  }

  navigateToAiSentence(): void {
    this.router.navigate(['/ai-learn/sentence']);
  }

  navigateToWrongBook(): void {
    this.router.navigate(['/wrong-book']);
  }

  navigateToUnknownBook(): void {
    this.router.navigate(['/unknown-book']);
  }

}
