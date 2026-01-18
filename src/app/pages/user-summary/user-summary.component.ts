import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzStatisticModule } from 'ng-zorro-antd/statistic';
import { NgxEchartsModule, NGX_ECHARTS_CONFIG } from 'ngx-echarts';
import { EChartsOption } from 'echarts';

import { SugarDictService } from '../../services/sugar-dict';
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
  imports: [CommonModule, NzGridModule, NzCardModule, NzStatisticModule, NgxEchartsModule],
  providers: [
    {
      provide: NGX_ECHARTS_CONFIG,
      useValue: {
        echarts: () => import('echarts'),
      },
    },
  ],
  templateUrl: './user-summary.component.html',
  styleUrl: './user-summary.component.css'
})
export class UserSummaryComponent {
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  statsData = {
    words: {
      mastered: 0, // 已掌握
      error: 0,     // 累计错误
      unknown: 0,   // 不认识
      custom: 0      // 自定义
    },
    sentences: {
      mastered: 0,
      error: 0,
      unknown: 0,
      custom: 0
    }
  };

  // 图表配置项
  wordsChartOption: EChartsOption = {};
  sentencesChartOption: EChartsOption = {};
  colorPalette = ['#52c41a', '#ff4d4f', '#faad14', '#1890ff'];
  initCharts() {
    // 1. 词汇图表配置
    this.wordsChartOption = this.getPieChartOption(
      '词汇统计',
      [
        { value: this.statsData.words.mastered, name: '已掌握' },
        { value: this.statsData.words.error, name: '累计错误' },
        { value: this.statsData.words.unknown, name: '不认识' },
        { value: this.statsData.words.custom, name: '自定义' }
      ]
    );

    // 2. 句子图表配置
    this.sentencesChartOption = this.getPieChartOption(
      '句子统计',
      [
        { value: this.statsData.sentences.mastered, name: '已掌握' },
        { value: this.statsData.sentences.error, name: '累计错误' },
        { value: this.statsData.sentences.unknown, name: '不认识' },
        { value: this.statsData.sentences.custom, name: '自定义' }
      ]
    );

  }
  getPieChartOption(title: string, data: { value: number; name: string }[]): EChartsOption {
    return {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)',
        confine: true
      },
      legend: {
        bottom: '0%',
        left: 'center',
        icon: 'circle'
      },
      color: this.colorPalette,
      series: [
        {
          name: title,
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data
        }
      ]
    };
  }
  ngOnInit(): void {
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
        //fill statsData
        this.statsData.words.mastered = learnedWords;
        this.statsData.words.error = mistakeWords;
        this.statsData.words.unknown = unknownWords;
        this.statsData.words.custom = customWords;
        this.statsData.sentences.mastered = learnedSentences;
        this.statsData.sentences.error = mistakeSentences;
        this.statsData.sentences.unknown = unknownSentences;
        this.statsData.sentences.custom = customSentences;
        //init charts
        this.initCharts();
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

}
