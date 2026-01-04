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
      const moduleId = params['moduleId'];
      this.moduleId = moduleId;
      if (!moduleId) return;
      this.sugarDictService.getSentencesByContentModuleId(moduleId, this.currentUser()?.id || -1).pipe(
        map((response: any) => {
          const rawData = response?.sentences || [];
          return rawData.map((item: any) => ({
            id: item.id,
            sentence_en: item.text,
            sentence_zh: item.textTranslation,
            audioUSUrl: item.audioUSUrl,
            audioUKUrl: item.audioUKUrl,
            showDetails: false,
            isKnown: item.isKnown || false
          } as Sentence));
        })
      ).subscribe({
        next: (processedData: Sentence[]) => {
          this.sentences = processedData;
          console.log('获取例句成功:', this.sentences);
        },
        error: (err) => {
          console.error('获取例句失败:', err);
        }
      });
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
    this.audio.src = apiUrl +"/audio/" +audioUrl;
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', e));
  }
}
