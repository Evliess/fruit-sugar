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
import { NzToolTipModule } from 'ng-zorro-antd/tooltip'; // 增加 Tooltip 提升体验
import { CiHuiService } from './ci-hui.service';
import { ActivatedRoute } from '@angular/router';
import { SugarDictService } from '../../services/sugar-dict';
import { map } from 'rxjs';
import { HoverSoundDirective } from '../../hover-sound.directive';




// 定义单词数据结构
interface VocabularyWord {
  id: number;
  word: string;
  usPhonetic: string; // 美式
  ukPhonetic: string; // 英式
  definition: {}; // 中文释义
  phrases: string[];  // 常用词组
  sentences: { en: string; zh: string }[]; // 例句
  showDetails: boolean; // 是否显示详情 (不认识时为 true)
  isKnown: boolean;     // 是否标记为认识
}


@Component({
  selector: 'app-ci-hui',
  standalone: true,
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
    HoverSoundDirective,
    NzToolTipModule
  ],
  templateUrl: './ci-hui.component.html',
  styleUrls: ['./ci-hui.component.css']
})
export class CiHuiComponent {
  isPracticeVisible = false;
  practiceWord: VocabularyWord | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;
  ciHuiService = inject(CiHuiService)
  private sugarDictService = inject(SugarDictService)
  private audio = new Audio();
  route = inject(ActivatedRoute);
  wordsCount = 0;
  words: any[] = [];

  constructor(private message: NzMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const isCustom = params['isCustom'] === 'true';
      const moduleId = params['moduleId'];
      this.sugarDictService.getWordsByChildContentModuleId(moduleId).pipe(
        map((response: any) => {
          const rawWords = response.words || [];
          return rawWords.map((wordData: any) => {
            const parsedPhrases = this.safeJsonParse(wordData.phrases, []);
            const formattedPhrases = parsedPhrases.map((p: any) =>
              `${p.text}; ${p.textTranslation}`
            );
            return {
              id: wordData.id,
              word: wordData.text,
              usPhonetic: wordData.phoneticUS,
              ukPhonetic: wordData.phoneticUK,
              definition: this.safeJsonParse(wordData.definition, {}),
              phrases: formattedPhrases,
              sentences: this.safeJsonParse(wordData.sentences, []),
              showDetails: false,
              isKnown: false
            } as VocabularyWord;
          });
        })
      ).subscribe({
        next: (words: VocabularyWord[]) => {
          this.words = words;
          this.wordsCount = words.length;
        },
        error: (err) => console.error('请求失败:', err)
      });
    });
  }

  private safeJsonParse(data: any, fallback: any): any {
    if (typeof data !== 'string') return data || fallback;
    try {
      return JSON.parse(data);
    } catch (e) {
      return fallback;
    }
  }

  handleSound(phonetic: string, word: string): void {
    if (phonetic=="US") {
      this.audio.src = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_us_female&txt=' + word;
    } else {
      this.audio.src = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_uk_male&txt=' + word;
    }
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', e));
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
    item.showDetails = true;
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

    if (this.practiceInput.trim().toLowerCase() === this.practiceWord.word.toLowerCase()) {
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