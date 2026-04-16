import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzTypographyModule } from 'ng-zorro-antd/typography';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { NzSwitchModule } from 'ng-zorro-antd/switch';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { Router } from '@angular/router';
import { MechanicalTypingDirective } from '../../mechanical-typing.directive';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';
import { map, switchMap } from 'rxjs';

interface MistakeItem {
  id: number;
  word: string;
  usPhonetic: string;
  ukPhonetic: string;
  definition: Record<string, string>;
  phrases: string[];
  sentences: { text: string; textTranslation: string }[];
  audioUSUrl?: string;
  audioUKUrl?: string;
  type: 'word' | 'sentence';
  definitions?: string; // 句子翻译
  textTranslation?: string; // 句子翻译
}

interface SentenceParts {
  before: string;
  word: string;
  after: string;
}

@Component({
  selector: 'app-wrong-book-listen',
  imports: [
    CommonModule,
    FormsModule,
    NzButtonModule,
    NzInputModule,
    NzIconModule,
    NzCardModule,
    NzGridModule,
    NzTypographyModule,
    NzSpaceModule,
    NzProgressModule,
    NzSwitchModule,
    NzDividerModule,
    MechanicalTypingDirective,
    NzDropDownModule,
    NzTagModule,
    NzSelectModule
  ],
  templateUrl: './wrong-book-listen.component.html',
  styleUrl: './wrong-book-listen.component.css',
})
export class WrongBookListenComponent implements OnInit {
  inputValue: string = '';
  inputStatus: string = 'minimal-input';
  isEyeOpen: boolean = false;
  isPlay: boolean = true;
  soundEnabled: boolean = true;
  soundTypeUK: boolean = true;
  private sugarDictService = inject(SugarDictService);
  private authService = inject(AuthService);
  private router = inject(Router);
  currentUser = this.authService.currentUser;

  items: MistakeItem[] = [];
  filteredItems: MistakeItem[] = [];
  currIndex: number = 0;
  currItem: MistakeItem = undefined as any;
  private audio = new Audio();
  hasError: boolean = false;
  sentenceParts: SentenceParts | null = null;

  // 模式选择：'all' | 'word' | 'sentence'
  mode: 'all' | 'word' | 'sentence' = 'all';

  ngOnInit(): void {
    this.loadMistakes();
  }

  loadMistakes(): void {
    this.sugarDictService.getUserMistake(this.currentUser()?.id || 0).pipe(
      map((response: any) => {
        const rawItems = response.words || [];
        return rawItems.map((itemData: any) => {
          const parsedPhrases = this.safeJsonParse(itemData.phrases, []);
          const formattedPhrases = parsedPhrases.map((p: any) =>
            `${p.text}; ${p.textTranslation}`
          );
          const mistakeItem: MistakeItem = {
            id: itemData.id,
            word: itemData.text,
            usPhonetic: itemData.phoneticUS,
            ukPhonetic: itemData.phoneticUK,
            audioUSUrl: itemData.audioUSUrl,
            audioUKUrl: itemData.audioUKUrl,
            definition: this.safeJsonParse(itemData.definition, {}),
            phrases: formattedPhrases,
            sentences: this.safeJsonParse(itemData.sentences, []),
            type: itemData.type || 'word',
            definitions: itemData.textTranslation,
            textTranslation: itemData.textTranslation,
          };
          return mistakeItem;
        });
      }),
      switchMap((items: MistakeItem[]) => {
        // 根据当前模式过滤
        this.items = items;
        this.applyModeFilter();
        // 加载听写进度
        const moduleId = `wrong-book-${this.mode}`;
        const type = this.mode === 'sentence' ? 'sentence' : 'word';
        return this.sugarDictService.getListenIndex(this.currentUser()?.id || -1, moduleId, type).pipe(
          map((response: any) => {
            const index = response.index || 0;
            this.currIndex = index >= 0 ? index : 0;
            if (this.filteredItems.length > 0) {
              this.currIndex = Math.min(this.currIndex, this.filteredItems.length - 1);
              this.currItem = this.filteredItems[this.currIndex];
              this.getSentenceWithGap();
              this.playAudio();
            }
            return items;
          })
        );
      })
    ).subscribe({
      next: () => {},
      error: (err) => console.error('请求失败:', err)
    });
  }

  applyModeFilter(): void {
    if (this.mode === 'all') {
      this.filteredItems = this.items;
    } else {
      this.filteredItems = this.items.filter(item => item.type === this.mode);
    }
    // 不重置索引，由loadProgress处理
    if (this.filteredItems.length > 0 && !this.currItem) {
      this.currItem = this.filteredItems[this.currIndex];
      this.getSentenceWithGap();
    }
  }

  onModeChange(): void {
    // 保存当前模式进度
    this.saveProgress();
    // 应用过滤
    this.applyModeFilter();
    // 加载新模式的进度
    this.loadProgress();
  }

  private loadProgress(): void {
    const moduleId = `wrong-book-${this.mode}`;
    const type = this.mode === 'sentence' ? 'sentence' : 'word';
    this.sugarDictService.getListenIndex(this.currentUser()?.id || -1, moduleId, type).subscribe({
      next: (response: any) => {
        const index = response.index || 0;
        this.currIndex = index >= 0 ? index : 0;
        if (this.filteredItems.length > 0) {
          this.currIndex = Math.min(this.currIndex, this.filteredItems.length - 1);
          this.currItem = this.filteredItems[this.currIndex];
          this.getSentenceWithGap();
          this.playAudio();
        }
      },
      error: (err) => console.error('加载进度失败:', err)
    });
  }

  getShortDefinition(item: MistakeItem): string {
    if (item.type === 'sentence') {
      return this.getSentenceTranslation(item);
    }
    const definitions = item.definition || {};
    for (const type in definitions) {
      if (definitions.hasOwnProperty(type)) {
        return (definitions[type] + '').split('；')[0];
      }
    }
    return '';
  }

  getSentenceTranslation(item: MistakeItem): string {
    if (item.type !== 'sentence') return '';

    // 首先尝试直接获取 textTranslation
    if (item.textTranslation) return item.textTranslation;
    if (item.definitions) return item.definitions;

    // 尝试从 definition 对象中获取
    if (item.definition && typeof item.definition === 'object') {
      // 检查是否有 textTranslation 字段
      if (item.definition['textTranslation']) {
        return item.definition['textTranslation'];
      }
      // 获取第一个值作为翻译
      for (const key in item.definition) {
        if (item.definition.hasOwnProperty(key)) {
          return item.definition[key];
        }
      }
    }

    return '';
  }

  getSentenceWithGap(): void {
    if (!this.currItem) {
      this.sentenceParts = null;
      return;
    }

    if (this.currItem.type === 'sentence') {
      // 句子模式：整句显示
      this.sentenceParts = {
        before: '',
        word: this.currItem.word,
        after: ''
      };
    } else {
      // 单词模式：从例句中提取包含该单词的句子
      const sentences = this.currItem.sentences || [];
      if (sentences.length > 0) {
        const sentence = sentences[0].text;
        const word = this.currItem.word;
        const wordIndex = sentence.toLowerCase().indexOf(word.toLowerCase());

        if (wordIndex !== -1) {
          this.sentenceParts = {
            before: sentence.substring(0, wordIndex),
            word: sentence.substring(wordIndex, wordIndex + word.length),
            after: sentence.substring(wordIndex + word.length)
          };
          return;
        }
      }
      this.sentenceParts = null;
    }
  }

  playAudio(): void {
    if (!this.currItem) return;

    const apiUrl = this.sugarDictService.openApiUrl;
    let audioUrl = '';

    if (this.currItem.type === 'word') {
      audioUrl = this.soundTypeUK ? (this.currItem.audioUKUrl || '') : (this.currItem.audioUSUrl || '');
      if (audioUrl) {
        this.audio.src = apiUrl + '/audio/words/' + audioUrl;
      }
    } else {
      audioUrl = this.soundTypeUK ? (this.currItem.audioUKUrl || '') : (this.currItem.audioUSUrl || '');
      if (audioUrl) {
        this.audio.src = apiUrl + '/audio/' + audioUrl;
      }
    }

    if (audioUrl) {
      this.audio.load();
      setTimeout(() => {
        this.audio.play().catch(_e => console.warn('Playback failed:', this.currItem.word));
      }, 20);
    }
  }

  playNext(): void {
    if (this.filteredItems.length === 0) return;
    this.currIndex = (this.currIndex + 1) % this.filteredItems.length;
    this.currItem = this.filteredItems[this.currIndex];
    this.inputValue = '';
    this.hasError = false;
    this.inputStatus = 'minimal-input';
    this.saveProgress();
    this.getSentenceWithGap();
    this.playAudio();
  }

  playPrev(): void {
    if (this.filteredItems.length === 0) return;
    this.currIndex = (this.currIndex - 1 + this.filteredItems.length) % this.filteredItems.length;
    this.currItem = this.filteredItems[this.currIndex];
    this.inputValue = '';
    this.hasError = false;
    this.inputStatus = 'minimal-input';
    this.saveProgress();
    this.getSentenceWithGap();
    this.playAudio();
  }

  private normalizeText(text: string): string {
    if (!text) return '';
    // 转为小写
    let normalized = text.toLowerCase();
    // 移除标点符号：保留字母、数字、空格和基本标点（如连字符）
    normalized = normalized.replace(/[^\w\s-]/g, ' ');
    // 将多个连续空格替换为单个空格
    normalized = normalized.replace(/\s+/g, ' ');
    // 去除首尾空格
    normalized = normalized.trim();
    return normalized;
  }

  checkAnswer(): void {
    if (!this.currItem) return;

    const normalizedInput = this.normalizeText(this.inputValue);
    const normalizedCorrectAnswer = this.normalizeText(this.currItem.word);

    if (normalizedInput === normalizedCorrectAnswer) {
      this.inputStatus = 'minimal-input minimal-input-success';
      this.hasError = false;
      setTimeout(() => {
        this.playNext();
      }, 2000);
    } else {
      this.inputStatus = 'minimal-input minimal-input-error';
      this.hasError = true;

      // 标记为错题（已在错题本中，无需重复标记）
      setTimeout(() => {
        this.playNext();
      }, 1000);
    }
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }

  goBackToWrongBook(): void {
    this.router.navigate(['/wrong-book']);
  }

  handleAction(action: string): void {
    if (action === 'play') {
      this.playAudio();
    } else if (action === 'prev') {
      this.playPrev();
    } else if (action === 'next') {
      this.playNext();
    }
  }

  toggleEye(): void {
    this.isEyeOpen = !this.isEyeOpen;
  }

  onInputClick(): void {
    this.hasError = false;
    this.inputStatus = 'minimal-input';
  }

  private safeJsonParse(data: any, fallback: any): any {
    if (typeof data !== 'string') return data || fallback;
    try {
      return JSON.parse(data);
    } catch (_e) {
      return fallback;
    }
  }

  private saveProgress(): void {
    const moduleId = `wrong-book-${this.mode}`;
    const type = this.mode === 'sentence' ? 'sentence' : 'word';
    this.sugarDictService.updateListenIndex(
      this.currentUser()?.id || -1,
      moduleId,
      type,
      this.currIndex
    ).subscribe({
      next: () => {},
      error: (err) => console.error('保存进度失败:', err)
    });
  }
}