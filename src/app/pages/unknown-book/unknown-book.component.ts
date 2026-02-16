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
import { ActivatedRoute } from '@angular/router';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';
import { HoverSoundDirective } from '../../hover-sound.directive';
import { map } from 'rxjs';

// 定义单词数据结构
interface VocabularyWord {
  id: number;
  word: string;
  usPhonetic: string;
  ukPhonetic: string;
  definition: Record<string, string>;
  phrases: string[];
  sentences: { text: string; textTranslation: string }[];
  showDetails: boolean;
  isKnown: boolean;
  type?: string;
  audioUSUrl?: string;
  audioUKUrl?: string;
}

@Component({
  selector: 'app-unknown-book',
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
  templateUrl: './unknown-book.component.html',
  styleUrl: './unknown-book.component.css'
})
export class UnknownBookComponent {
  constructor(private message: NzMessageService) { }
  isPracticeVisible = false;
  practiceWord: VocabularyWord | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  private audio = new Audio();
  currentUser = this.authService.currentUser
  wordsCount = 0;
  words: VocabularyWord[] = [];


  route = inject(ActivatedRoute);
  ngOnInit(): void {
    this.sugarDictService.getUserUnknown(this.currentUser()?.id || 0).pipe(
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
            audioUSUrl: wordData.audioUSUrl,
            audioUKUrl: wordData.audioUKUrl,
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

  handleSound(phonetic: string, item: VocabularyWord): void {
    const apiUrl = this.sugarDictService.apiUrl;
    if (item.type == "word") {
      if (phonetic == "US") {
        this.audio.src = apiUrl + "/audio/words/" + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + "/audio/words/" + item.audioUKUrl;
      }
    } else {
      if (phonetic == "US") {
        this.audio.src = apiUrl + "/audio/" + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + "/audio/" + item.audioUKUrl;
      }
    }
    this.audio.load();
    this.audio.play().catch(e => {
      console.warn('Playback failed:', item.word);
    });
  }

  // 切换显示全部/收起
  toggleDetails(item: VocabularyWord): void {
    item.showDetails = !item.showDetails;
  }

  // 点击“认识”
  // markAsKnown(item: VocabularyWord): void {
  //   item.isKnown = true;
  //   item.showDetails = false; // 收起详情
  //   this.message.success('太棒了！已标记为认识。');
  // }

  // 点击“不认识”
  // markAsUnknown(item: VocabularyWord): void {
  //   item.showDetails = true;
  //   item.isKnown = false;
  // }

  deleteUnknownWord(item: VocabularyWord): void {
    this.sugarDictService.removeUserUnknownWord(this.currentUser()?.id || -1, item.id).subscribe({
      next: (response: any) => {
        this.words = this.words.filter(w => w.id !== item.id);
        this.wordsCount = this.words.length;
        this.message.success('已从生词本移除该单词。');
      }});
  }

  deleteUnknownSentence(item: VocabularyWord): void {
    this.sugarDictService.removeUserUnknownSentence(this.currentUser()?.id || -1, item.id).subscribe({
      next: (response: any) => {  
        this.words = this.words.filter(w => w.id !== item.id);
        this.wordsCount = this.words.length;
        this.message.success('已从生词本移除该例句。');
      }});
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
