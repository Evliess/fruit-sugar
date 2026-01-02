import { Component, inject } from '@angular/core';
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
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { ActivatedRoute } from '@angular/router';
import { MechanicalTypingDirective } from '../../mechanical-typing.directive';
import { SugarDictService } from '../../services/sugar-dict';
import { map } from 'rxjs';

interface Sentence {
  id: number;
  word: string;
  definitions: string;// 词性
  usAudio: string; // 美式
  ukAudio: string; // 英式
}

@Component({
  selector: 'app-sen-listen-write',
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
    NzSwitchModule,
    NzDividerModule,
    MechanicalTypingDirective,
    NzDropDownModule,
    NzTagModule
  ],
  templateUrl: './sen-listen-write.component.html',
  styleUrl: './sen-listen-write.component.css'
})
export class SenListenWriteComponent {
  inputValue: string = '';
  inputStatus: string = 'minimal-input';
  isEyeOpen: boolean = false;
  isPlay: boolean = true;
  soundEnabled: boolean = true;
  soundTypeUK: boolean = true;
  private sugarDictService = inject(SugarDictService)
  route = inject(ActivatedRoute);
  apiUrl = this.sugarDictService.apiUrl;

  words: Sentence[] = [];
  currIndex: number = 0;
  currWord: Sentence = undefined as any;
  private audio = new Audio();

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const moduleId = params['moduleId'];
      this.sugarDictService.getSentencesByContentModuleId(1).pipe(
        map((response: any) => {
          const rawWords = response.sentences || [];
          return rawWords.map((sentence: any) => {
            return {
              id: sentence.id,
              word: sentence.text,
              usAudio: sentence.audioUSUrl,
              ukAudio: sentence.audioUKUrl,
              definitions: sentence.textTranslation,
            } as Sentence;
          });
        })
      ).subscribe({
        next: (words: Sentence[]) => {
          this.words = words;
          this.currWord = this.words[this.currIndex];
          if (this.soundTypeUK) {
            this.playAudio(this.currWord.ukAudio);
          } else {
            this.playAudio(this.currWord.usAudio);
          }
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


  playAudio(audioUrl: string): void {
    const apiUrl = this.sugarDictService.apiUrl;
    this.audio.src = apiUrl + "/audio/" + audioUrl;
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', e));
  }

  playNext(): void {
    this.currIndex = (this.currIndex + 1) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    if (this.soundTypeUK) {
      this.playAudio(this.currWord.ukAudio);
    } else {
      this.playAudio(this.currWord.usAudio);
    }
  }

  playPrev(): void {
    this.currIndex = (this.currIndex - 1 + this.words.length) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    if (this.soundTypeUK) {
      this.playAudio(this.currWord.ukAudio);
    } else {
      this.playAudio(this.currWord.usAudio);
    }
  }

  checkAnswer(): void {
    const trimmedInput = this.inputValue.trim().toLowerCase();
    const correctAnswer = this.currWord.word.toLowerCase();
    if (trimmedInput === correctAnswer) {
      this.inputStatus = 'minimal-input minimal-input-success';
      this.playNext();
    } else {
      this.inputStatus = 'minimal-input';
    }
  }

  // 模拟点击事件
  handleAction(action: string): void {
    if ("play" == action) {
      this.togglePlay();
      if (this.soundTypeUK) {
        this.playAudio(this.currWord.ukAudio);
      } else {
        this.playAudio(this.currWord.usAudio);
      }
    };
    if ("prev" == action) { this.playPrev(); };
    if ("next" == action) { this.playNext(); }
  }

  togglePlay(): void {
    this.isPlay = !this.isPlay;
  }

  toggleEye(): void {
    this.isEyeOpen = !this.isEyeOpen;
  }

}
