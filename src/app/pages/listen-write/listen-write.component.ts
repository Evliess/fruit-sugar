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
import { AuthService } from '../../services/auth';
import { map } from 'rxjs';



interface VocabularyWord {
  id: number;
  word: string;
  definitions: { type: string; zh: string }[];// 词性
  usAudio: string; // 美式
  ukAudio: string; // 英式
}

@Component({
  selector: 'app-listen-write',
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
  templateUrl: './listen-write.component.html',
  styleUrl: './listen-write.component.css',
})
export class ListenWriteComponent {
  inputValue: string = '';
  inputStatus: string = 'minimal-input';
  isEyeOpen: boolean = false;
  isPlay: boolean = true;
  soundEnabled: boolean = true;
  soundTypeUK: boolean = true;
  private sugarDictService = inject(SugarDictService)
  route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  words: VocabularyWord[] = [];
  moduleId: string = ''; 
  currIndex: number = 0;
  currWord: VocabularyWord = undefined as any;
  private audio = new Audio();

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const moduleId = params['moduleId'];
      this.moduleId = moduleId;
      this.sugarDictService.getWordsSimpleByChildContentModuleId(moduleId).pipe(
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
              usAudio: wordData.phoneticUS,
              ukAudio: wordData.phoneticUK,
              definitions: this.safeJsonParse(wordData.definition, {}),
            } as VocabularyWord;
          });
        })
      ).subscribe({
        next: (words: VocabularyWord[]) => {
          this.words = words;
          this.currWord = this.words[this.currIndex];
          this.playAudio(this.currWord.word);
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


  playAudio(word: string): void {
    const usAudio = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_us_female&txt=' + word;
    const ukAudio = 'https://api.frdic.com/api/v2/speech/speakweb?langid=en&voicename=en_uk_male&txt=' + word;
    this.audio.src = this.soundTypeUK ? ukAudio : usAudio;
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', word));
  }

  playNext(): void {
    this.currIndex = (this.currIndex + 1) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.playAudio(this.currWord.word);
  }

  playPrev(): void {
    this.currIndex = (this.currIndex - 1 + this.words.length) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.playAudio(this.currWord.word);
  }

  checkAnswer(): void {
    const trimmedInput = this.inputValue.trim().toLowerCase();
    const correctAnswer = this.currWord.word.toLowerCase();
    if (trimmedInput === correctAnswer) {
      this.inputStatus = 'minimal-input minimal-input-success';
      this.playNext();
    } else {
      this.inputStatus = 'minimal-input';
      this.sugarDictService.markWordAsMistake(this.currentUser()?.id || -1, this.currWord.id, this.moduleId).subscribe({
      next: (response: any) => {
        console.log("Succeed to mark a word as mistake");
      }
    });
    }
  }

  // 模拟点击事件
  handleAction(action: string): void {
    if ("play" == action) {
      this.togglePlay();
      this.playAudio(this.currWord.word);
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
