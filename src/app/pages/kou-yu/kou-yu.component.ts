import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Ng-Zorro Imports
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { ActivatedRoute } from '@angular/router';


// 定义单词数据结构
interface VocabularyWord {
  id: number;
  sentence_en: string;
  sentence_zh: string; // 中文释义
  showDetails: boolean; // 是否显示详情 (不认识时为 true)
  isKnown: boolean;     // 是否标记为认识
}

@Component({
  selector: 'app-kou-yu',
  imports: [
    CommonModule,
    FormsModule,
    NzGridModule,
    NzCardModule,
    NzButtonModule,
    NzTagModule,
    NzModalModule,
    NzInputModule,
    NzIconModule,
    NzDividerModule,
    NzToolTipModule
  ],
  templateUrl: './kou-yu.component.html',
  styleUrl: './kou-yu.component.css'
})
export class KouYuComponent {
  isPracticeVisible = false;
  practiceWord: VocabularyWord | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;

  route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const isCustom = params['isCustom'];
      this.isCustom = isCustom === 'true';
    });
  }

  // 模拟数据：10个单词
  words: VocabularyWord[] = [
    {
      id: 1,
      sentence_en: 'The Bodhi tree originally has no roots.',
      sentence_zh: '菩提本无树',
      showDetails: false,
      isKnown: false
    },
    {
      id: 2,
      sentence_en: 'Even a clear mirror is not a platform',
      sentence_zh: '明镜亦非台',
      showDetails: false,
      isKnown: false
    },
  ];

  // 填充剩余数据的辅助代码（实际开发中请替换为真实数据）
  constructor(private message: NzMessageService) {
    for (let i = 3; i <= 10; i++) {
      this.words.push({
        id: i,
        sentence_en: `Sample sentence ${i}`,
        sentence_zh: `这是例句 ${i} 。`,
        showDetails: false,
        isKnown: false
      });
    }
  }

  // 切换显示全部/收起
  toggleDetails(item: VocabularyWord): void {
    item.showDetails = !item.showDetails;
  }

  // 点击“认识”
  markAsKnown(item: VocabularyWord): void {
    item.isKnown = true;
    item.showDetails = false; // 收起详情
    this.message.success('太棒了！已标记为认识。');
  }

  // 点击“不认识”
  markAsUnknown(item: VocabularyWord): void {
    item.showDetails = false;
    item.isKnown = false;
  }

  // 点击“练一练”
  openPractice(item: VocabularyWord): void {
    this.practiceWord = item;
    this.practiceInput = ''; // 清空输入框
    this.isPracticeVisible = true;
  }

  // 提交练习
  handlePracticeOk(): void {
    if (!this.practiceWord) return;

    if (this.practiceInput.trim().toLowerCase() === this.practiceWord.sentence_en.toLowerCase()) {
      this.message.success('拼写正确！继续保持！');
      this.isPracticeVisible = false;
    } else {
      this.message.error('拼写有误，请再试一次。');
    }
  }

  handlePracticeCancel(): void {
    this.isPracticeVisible = false;
  }
}
