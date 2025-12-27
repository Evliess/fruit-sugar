import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { SugarDictService } from '../../services/sugar-dict';

import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, NzGridModule, NzCardModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor(private router: Router) { }
  sentenceCases: any[] = []
  wordCases: any[] = []
  private sugarDictService = inject(SugarDictService)
  sections: any[] = [];

  ngOnInit(): void {
    this.sugarDictService.getAllContentModules().subscribe({
      next: (response: any) => {
        this.sentenceCases = response.sentenceCases;
        this.wordCases = response.wordCases;
        this.sections = [];
        this.sections.push({
          name: '场景词汇',
          items: this.wordCases
        });
        this.sections.push({
          name: '去听写',
          items: this.wordCases
        });
        this.sections.push({
          name: '口语表达',
          items: this.sentenceCases
        });
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

}
