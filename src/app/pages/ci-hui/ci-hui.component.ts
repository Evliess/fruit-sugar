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
import { AuthService } from '../../services/auth';
import { map } from 'rxjs';
import { HoverSoundDirective } from '../../hover-sound.directive';

// 定义单词数据结构
interface VocabularyWord {
  id: number;
  word: string;
  audioUSUrl: string; // 美式发音 URL
  audioUKUrl: string; // 英式发音 URL
  usPhonetic: string; // 美式
  ukPhonetic: string; // 英式
  definition: {}; // 中文释义
  phrases: string[];  // 常用词组
  sentences: { en: string; zh: string }[]; // 例句
  showDetails: boolean; // 是否显示详情 (不认识时为 true)
  isKnown: boolean;     // 是否标记为认识
  type: string;         // 单词类型 (e.g., built-in, custom)
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
  isLoading = false;
  ciHuiService = inject(CiHuiService)
  private sugarDictService = inject(SugarDictService)
  private audio = new Audio();
  private authService = inject(AuthService);
  route = inject(ActivatedRoute);
  wordsCount = 0;
  words: any[] = [];
  moduleId = '';
  currentUser = this.authService.currentUser;

  constructor(private message: NzMessageService) { }

  ngOnInit(): void {
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

  /**
   * Map raw word data to VocabularyWord interface
   */
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

  /**
   * Fetch words for a specific module
   */
  private fetchModuleWords(moduleId: number, userId: number): void {
    this.sugarDictService.getWordsByChildContentModuleId(Number(moduleId), userId).pipe(
      map((response: any) => this.mapWordData(response.words || [], 'built-in'))
    ).subscribe({
      next: (words: VocabularyWord[]) => {
        this.words = words;
        this.wordsCount = words.length;
      },
      error: (err) => console.error('获取模块单词失败:', err)
    });
  }

  /**
   * Fetch custom words for the current user
   */
  private fetchCustomWords(userId: number): void {
    this.sugarDictService.getCustomWords(userId).pipe(
      map((response: any) => this.mapWordData(response.words || [], 'custom'))
    ).subscribe({
      next: (words: VocabularyWord[]) => {
        this.words = words;
        this.wordsCount = words.length;
      },
      error: (err) => console.error('获取自定义单词失败:', err)
    });
  }

  /**
   * Clear words and reset count when no valid data is available
   */
  private clearWords(): void {
    this.words = [];
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
      next: (response: any) => {
        this.message.success('自定义单词成功！');
        this.userWord = ''; // 清空输入框
        this.isLoading = false;
        // 刷新单词列表
        this.ngOnInit();
      },
      error: (err) => {
        console.error('请求失败:', err);
        this.isLoading = false;
      }
    });
  }

  handleSound(phonetic: string, item: VocabularyWord): void {
    const apiUrl = this.sugarDictService.apiUrl;
    if(item.type == "custom") {
      if(phonetic == "US") {
        this.audio.src = apiUrl + "/audio/custom/" + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + "/audio/custom/" + item.audioUKUrl;
      }
    } else {
      if(phonetic == "US") {
        this.audio.src = apiUrl + "/audio/words/" + item.audioUSUrl;
      } else {
        this.audio.src = apiUrl + "/audio/words/" + item.audioUKUrl;
      }
    }  
    this.audio.load();
    this.audio.play().catch(e => {
      console.warn('Playback failed:', item.word);
      this.sugarDictService.fixBuiltInWordTts(item.word).subscribe({
        next: (response: any) => {
          this.ngOnInit();
        },
        error: (err) => console.error('修复失败:', err)
      });
    });
  }

  deleteCustomBook(item: VocabularyWord): void {
    this.sugarDictService.deleteCustomBook(this.currentUser()?.id || -1, item.id, "word").subscribe({
      next: (response: any) => {
        this.message.success('删除成功！');
        this.ngOnInit();
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

  // 切换显示全部/收起
  toggleDetails(item: VocabularyWord): void {
    item.showDetails = !item.showDetails;
  }

  // 点击“认识”
  markAsKnown(item: VocabularyWord): void {
    item.isKnown = true;
    item.showDetails = false; // 收起详情
    this.sugarDictService.markWordAsKnown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: (response: any) => {
        this.message.success('太棒了！已标记为认识。');
      }
    });
  }

  // 点击“不认识”
  markAsUnknown(item: VocabularyWord): void {
    item.showDetails = true;
    item.isKnown = false;
    this.sugarDictService.markWordAsUnknown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: (response: any) => {
        this.message.info('已标记为不认识，加油学习哦！');
      }
    });
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