import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
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
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { ActivatedRoute } from '@angular/router';
import { HoverSoundDirective } from '../../hover-sound.directive';
import { AuthService } from '../../services/auth';
import { SugarDictService } from '../../services/sugar-dict';
import { map } from 'rxjs/operators';

// 定义单词数据结构
interface VocabularyWord {
  id: number;
  word: string;
  usPhonetic: string; // 美式
  ukPhonetic: string; // 英式
  definition: string; // 中文释义
  phrases: string[];  // 常用词组
  sentences: { text: string; textTranslation: string }[]; // 例句
  showDetails: boolean; // 是否显示详情 (不认识时为 true)
  isKnown: boolean;     // 是否标记为认识
  type?: string;    // 单词类型（可选）
}

@Component({
  selector: 'app-wrong-book',
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
  templateUrl: './wrong-book.component.html',
  styleUrl: './wrong-book.component.css'
})
export class WrongBookComponent {
  isPracticeVisible = false;
  practiceWord: VocabularyWord | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  private audio = new Audio();
  currentUser = this.authService.currentUser;

  route = inject(ActivatedRoute);
  words: VocabularyWord[] = [];
  wordsCount = 0;
  constructor(private message: NzMessageService, private notification: NzNotificationService) { }
  ngOnInit(): void {
    this.sugarDictService.getUserMistake(this.currentUser()?.id || 0).pipe(
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
            isKnown: wordData.isKnown,
            type: wordData.type
          } as VocabularyWord;
        });
      })
    ).subscribe({
      next: (words: VocabularyWord[]) => {
        this.words = words;
        this.wordsCount = words.length;
        this.notification.create(
          'info',
          '提示：',
          '根据艾宾浩斯记忆法，建议你先复习这些单词，它们的记忆间隔较短',
          { nzPlacement: "top" }
        );
      },
      error: (err) => console.error('请求失败:', err)
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

  toggleDetails(item: VocabularyWord): void {
    item.showDetails = !item.showDetails;
  }

  // 点击“认识”
  markAsKnown(item: VocabularyWord): void {
    item.isKnown = true;
    item.showDetails = false; // 收起详情
    this.message.success('太棒了！已标记为认识。');
  }

  deleteMistakeWord(item: VocabularyWord): void {
    this.sugarDictService.removeUserMistakeWord(this.currentUser()?.id || 0, item.id).subscribe({
      next: (response: any) => {
        this.message.success('已删除单词');
        this.words = this.words.filter(w => w.id !== item.id);
        this.wordsCount--;
      },
      error: (err) => console.error('删除失败:', err)
    });
  }

  deleteMistakeSentence(item: VocabularyWord): void {
    this.sugarDictService.removeUserMistakeSentence(this.currentUser()?.id || 0, item.id).subscribe({
      next: (response: any) => {
        this.message.success('已删除例句');
        this.words = this.words.filter(w => w.id !== item.id);
        this.wordsCount--;
      },
      error: (err) => console.error('删除失败:', err)
    });
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


  handleSound(phonetic: string, word: string): void {
    if (phonetic == "US") {
      this.audio.src = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_us_female&txt=' + word;
    } else {
      this.audio.src = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_uk_male&txt=' + word;
    }
    this.audio.load();
    this.audio.play().catch(e => {
      console.warn('Playback failed:', word);
    });
  }
}
