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
import { map } from 'rxjs';
import { HoverSoundDirective } from '../../hover-sound.directive';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';


interface Sentence {
  id: number;
  sentence_en: string;
  sentence_zh: string;
  showDetails: boolean;
  audioUSUrl: string;
  audioUKUrl: string;
  isKnown: boolean;
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
    HoverSoundDirective,
    NzToolTipModule
  ],
  templateUrl: './kou-yu.component.html',
  styleUrl: './kou-yu.component.css'
})
export class KouYuComponent {
  isPracticeVisible = false;
  practiceWord: Sentence | null = null;
  practiceInput = '';
  userWord = '';
  isCustom = false;
  isLoading = false;
  moduleId = '';
  route = inject(ActivatedRoute);
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;
  private audio = new Audio();
  sentences: Sentence[] = [];
  constructor(private message: NzMessageService) { }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.isCustom = params['isCustom'] === 'true';
      const userId = this.currentUser()?.id || -1;
      if (this.isCustom) {
        this.fetchCustomSentences(userId);
      } else {
        const moduleId = params['moduleId'];
        if (moduleId) {
          this.fetchModuleSentences(moduleId, userId);
        } else {
          this.sentences = [];
        }
      }
    });
  }

  /**
   * Map raw sentence data to Sentence interface
   */
  private mapSentenceData(rawData: any[]): Sentence[] {
    return rawData.map((item: any) => ({
      id: item.id,
      sentence_en: item.text,
      sentence_zh: item.textTranslation,
      audioUSUrl: item.audioUSUrl,
      audioUKUrl: item.audioUKUrl,
      showDetails: false,
      isKnown: item.isKnown || false
    } as Sentence));
  }

  /**
   * Fetch custom sentences for the current user
   */
  private fetchCustomSentences(userId: number): void {
    this.sugarDictService.getCustomSentences(userId).pipe(
      map((response: any) => this.mapSentenceData(response?.sentences || []))
    ).subscribe({
      next: (processedData: Sentence[]) => {
        this.sentences = processedData;
        console.log('获取自定义例句成功:', this.sentences);
      },
      error: (err) => {
        console.error('获取自定义例句失败:', err);
      }
    });
  }

  /**
   * Fetch sentences by content module ID
   */
  private fetchModuleSentences(moduleId: number, userId: number): void {
    this.moduleId = moduleId + "";
    this.sugarDictService.getSentencesByContentModuleId(moduleId, userId).pipe(
      map((response: any) => this.mapSentenceData(response?.sentences || []))
    ).subscribe({
      next: (processedData: Sentence[]) => {
        this.sentences = processedData;
        console.log('获取例句成功:', this.sentences);
      },
      error: (err) => {
        console.error('获取例句失败:', err);
      }
    });
  }

  // 添加自定义例句
  addCustom(sentence: string) {
    if (!sentence.trim()) {
      this.message.warning('请输入要添加的例句。');
      return;
    }
    this.isLoading = true;
    this.sugarDictService.customSentence(this.currentUser()?.id || -1, sentence).subscribe({
      next: (response: any) => {
        this.message.success('自定义例句添加成功！');
        this.isLoading = false;
        this.ngOnInit(); // 刷新数据
      },
      error: (err) => {
        this.isLoading = false;
        console.error('添加自定义例句失败:', err);
        this.message.error('添加自定义例句失败，请稍后重试。');
        
      }
    });
  }

  // 切换显示全部/收起
  toggleDetails(item: Sentence): void {
    item.showDetails = !item.showDetails;
  }

  // 点击“认识”
  markAsKnown(item: Sentence): void {
    item.isKnown = true;
    item.showDetails = false;
    this.sugarDictService.markSentenceAsKnown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: (response: any) => {
        this.message.success('太棒了！已标记为认识。');
      }
    });
  }

  // 点击“不认识”
  markAsUnknown(item: Sentence): void {
    item.showDetails = false;
    item.isKnown = false;
    this.sugarDictService.markSentenceAsUnKnown(this.currentUser()?.id || -1, item.id, this.moduleId).subscribe({
      next: (response: any) => {
        this.message.info('已标记为不认识，加油学习哦！');
      }
    });
  }

  deleteCustomSentence(item: Sentence): void {
    console.log(item); 
    this.sugarDictService.deleteCustomBook(this.currentUser()?.id || -1, item.id, "sentence").subscribe({
      next: (response: any) => {
        this.message.success('自定义例句删除成功！');
        this.ngOnInit(); // 刷新数据
      },
      error: (err) => {
        console.error('删除自定义例句失败:', err);
        this.message.error('删除自定义例句失败，请稍后重试。');
      }
    });
  }

  // 点击“练一练”
  openPractice(item: Sentence): void {
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

  handleSound(audioUrl: string): void {
    const apiUrl = this.sugarDictService.apiUrl;
    if(audioUrl.endsWith(".mp3")) {
      this.audio.src =apiUrl + "/audio/custom/" + audioUrl;
    } else {
      this.audio.src = apiUrl + "/audio/" + audioUrl;
    }
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', e));
  }
}
