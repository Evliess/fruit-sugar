import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { map } from 'rxjs';

import { CiHuiService } from './ci-hui.service';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';
import { HoverSoundDirective } from '../../hover-sound.directive';

interface VocabularyWord {
  id: number;
  word: string;
  audioUSUrl: string;
  audioUKUrl: string;
  usPhonetic: string;
  ukPhonetic: string;
  definition: {};
  phrases: string[];
  sentences: { en: string; zh: string }[];
  showDetails: boolean;
  isKnown: boolean;
  type: string;
}

@Component({
  selector: 'app-ci-hui-list1',
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
  templateUrl: './ci-hui-list1.component.html',
  styleUrls: ['./ci-hui-list1.component.css']
})
export class CiHuiList1Component {
  isPracticeVisible = false;
  practiceWord: VocabularyWord | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;
  isLoading = false;
  ciHuiService = inject(CiHuiService);
  private sugarDictService = inject(SugarDictService);
  private audio = new Audio();
  private authService = inject(AuthService);
  route = inject(ActivatedRoute);
  wordsCount = 0;
  firstWords: any[] = [];
  remainingWords: any[] = [];
  moduleId = '';
  currentUser = this.authService.currentUser;

  get words(): any[] {
    return [...this.firstWords, ...this.remainingWords];
  }

  constructor(private message: NzMessageService) {}

  ngOnInit(): void {
    this.ciHuiService.setLearnMode(false);

    this.route.queryParams.subscribe(params => {
      this.isCustom = params['isCustom'] === 'true';
      const userId = this.currentUser()?.id || 0;

      if (this.isCustom) {
        this.fetchCustomWords(userId);
      } else {
        const moduleId = params['moduleId'];
        this.moduleId = moduleId;

        if (moduleId) {
          this.fetchModuleWords(moduleId, userId);
        } else {
          this.clearWords();
        }
      }
    });
  }

  private mapWordData(rawWords: any[], type: string): VocabularyWord[] {
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
        type: type
      } as VocabularyWord;
    });
  }

  private fetchModuleWords(moduleId: number, userId: number): void {
    this.sugarDictService.getWordsByChildContentModuleId(Number(moduleId), userId).pipe(
      map((response: any) => {
        const rawWords = response.words || [];
        const firstBatch = rawWords.slice(0, 10);
        const remainingBatch = rawWords.slice(10);

        this.firstWords = this.mapWordData(firstBatch, 'built-in');
        this.remainingWords = [];
        this.wordsCount = rawWords.length;

        setTimeout(() => {
          this.remainingWords = this.mapWordData(remainingBatch, 'built-in');
        }, 20);

        return this.firstWords;
      })
    ).subscribe({
      next: () => {},
      error: (err) => console.error('获取模块单词失败:', err)
    });
  }

  private fetchCustomWords(userId: number): void {
    this.sugarDictService.getCustomWords(userId).pipe(
      map((response: any) => {
        const rawWords = response.words || [];
        const firstBatch = rawWords.slice(0, 10);
        const remainingBatch = rawWords.slice(10);

        this.firstWords = this.mapWordData(firstBatch, 'custom');
        this.remainingWords = [];
        this.wordsCount = rawWords.length;

        setTimeout(() => {
          this.remainingWords = this.mapWordData(remainingBatch, 'custom');
        }, 20);

        return this.firstWords;
      })
    ).subscribe({
      next: () => {},
      error: (err) => console.error('获取自定义单词失败:', err)
    });
  }

  private clearWords(): void {
    this.firstWords = [];
    this.remainingWords = [];
    this.wordsCount = 0;
  }

  private safeJsonParse(data: any, fallback: any): any {
    if (typeof data !== 'string') return data || fallback;
    try {
      return JSON.parse(data);
    } catch (e) {
      return fallback;
    }
  }

  customWord(word: string): void {
    if (!word.trim()) {
      this.message.error('请输入要自定义的单词');
      return;
    }
    this.isLoading = true;
    this.sugarDictService.customWord(this.currentUser()?.id || -1, word).subscribe({
      next: () => {
        this.message.success('自定义单词成功！');
        this.userWord = '';
        this.isLoading = false;
        this.ngOnInit();
      },
      error: (err) => {
        console.error('请求失败:', err);
        this.isLoading = false;
      }
    });
  }

  handleSound(phonetic: string, item: VocabularyWord): void {
    const apiUrl = this.sugarDictService.openApiUrl;
    if (item.type == 'custom') {
      if (phonetic == 'US') {
        this.audio.src = apiUrl + '/audio/custom/' + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + '/audio/custom/' + item.audioUKUrl;
      }
    } else {
      if (phonetic == 'US') {
        this.audio.src = apiUrl + '/audio/words/' + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + '/audio/words/' + item.audioUKUrl;
      }
    }
    this.audio.load();
    this.audio.play().catch(e => {
      console.warn('Playback failed:', item.word);
      this.sugarDictService.fixBuiltInWordTts(item.word).subscribe({
        next: (response: any) => {
          if (response && response.data) {
            const updatedWord = response.data;
            item.audioUSUrl = updatedWord.audioUSUrl || item.audioUSUrl;
            item.audioUKUrl = updatedWord.audioUKUrl || item.audioUKUrl;
            this.handleSound(phonetic, item);
          }
        },
        error: (err) => console.error('修复失败:', err)
      });
    });
  }

  deleteCustomBook(item: VocabularyWord): void {
    this.sugarDictService.deleteCustomBook(this.currentUser()?.id || -1, item.id, 'word').subscribe({
      next: () => {
        this.message.success('删除成功！');
        this.ngOnInit();
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

  markAsKnown(item: VocabularyWord): void {
    item.isKnown = true;
    item.showDetails = false;
    this.sugarDictService.markWordAsKnown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: () => {}
    });
  }

  markAsUnknown(item: VocabularyWord): void {
    item.showDetails = true;
    item.isKnown = false;
    this.sugarDictService.markWordAsUnknown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: () => {}
    });
  }

  openPractice(item: VocabularyWord): void {
    this.practiceWord = item;
    this.practiceInput = '';
    this.isPracticeVisible = true;
  }

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

  simpleWordType(type: any): string {
    if (type.includes('noun')) {
      return 'n.';
    } else if (type.includes('adjective')) {
      return 'adj.';
    } else if (type.includes('verb')) {
      return 'v.';
    } else if (type.includes('adverb')) {
      return 'adv.';
    } else if (type.includes('phrase')) {
      return 'phrase';
    } else {
      return type;
    }
  }
}

