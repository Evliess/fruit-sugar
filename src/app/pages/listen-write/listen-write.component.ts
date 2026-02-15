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
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { ActivatedRoute, Router } from '@angular/router';
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
  audioUSUrl: string; // 美式音频URL
  audioUKUrl: string; // 英式音频URL
  sentences: string;
}

interface SentenceParts {
  before: string;
  word: string;
  after: string;
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
    NzProgressModule,
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
  private router = inject(Router);
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  words: VocabularyWord[] = [];
  moduleId: string = ''; 
  currIndex: number = 0;
  currWord: VocabularyWord = undefined as any;
  private audio = new Audio();
  hasError: boolean = false;
  sentenceParts: SentenceParts | null = null;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const moduleId = params['moduleId'];
      this.moduleId = moduleId;
      this.sugarDictService.getWordsSimpleByChildContentModuleId(moduleId).pipe(
        map((response: any) => {
          const rawWords = response.words || [];
          return rawWords.map((wordData: any) => {
            return {
              id: wordData.id,
              word: wordData.text,
              usAudio: wordData.phoneticUS,
              ukAudio: wordData.phoneticUK,
              audioUSUrl: wordData.audioUSUrl,
              audioUKUrl: wordData.audioUKUrl,
              definitions: this.safeJsonParse(wordData.definition, {}),
              sentences: this.safeJsonParse(wordData.sentences, {})[0].text,
            } as VocabularyWord;
          });
        })
      ).subscribe({
        next: (words: VocabularyWord[]) => {
          this.words = words;
          this.currWord = this.words[this.currIndex];
          this.getSentenceWithGap();
          this.playAudio(this.currWord);
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

  getShortDefinition(word: VocabularyWord): string {
    const definitions = word.definitions || {};
    for (const type in definitions) {
      if (definitions.hasOwnProperty(type)) {
        return (definitions[type]+"").split('；')[0];
      }
    }
    return '';
  }

  getSentenceWithGap(): void {
    if (!this.currWord || !this.currWord.sentences) {
      this.sentenceParts = null;
      return;
    }
    const sentence = this.currWord.sentences;
    const word = this.currWord.word;
    const wordIndex = sentence.toLowerCase().indexOf(word.toLowerCase());
    
    if (wordIndex === -1) {
      this.sentenceParts = null;
      return;
    }
    
    this.sentenceParts = {
      before: sentence.substring(0, wordIndex),
      word: sentence.substring(wordIndex, wordIndex + word.length),
      after: sentence.substring(wordIndex + word.length)
    };
  }

  sound(): void{
    this.playAudio(this.currWord);
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }


  playAudio(word: VocabularyWord): void {
    const apiUrl = this.sugarDictService.apiUrl;
    this.audio.src = this.soundTypeUK ? apiUrl + "/audio/words/" + word.audioUKUrl : apiUrl + "/audio/words/" + word.audioUSUrl;
    this.audio.load();
    setTimeout(() => {
      this.audio.play().catch(e => {console.warn('Playback failed:', word)});
    }, 20);
  }

  playNext(): void {
    this.currIndex = (this.currIndex + 1) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.hasError = false;
    this.inputStatus = 'minimal-input';
    this.getSentenceWithGap();
    this.playAudio(this.currWord);
  }

  playPrev(): void {
    this.currIndex = (this.currIndex - 1 + this.words.length) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.hasError = false;
    this.inputStatus = 'minimal-input';
    this.getSentenceWithGap();
    this.playAudio(this.currWord);
  }

  checkAnswer(): void {
    const trimmedInput = this.inputValue.trim().toLowerCase();
    const correctAnswer = this.currWord.word.toLowerCase();
    if (trimmedInput === correctAnswer) {
      this.inputStatus = 'minimal-input minimal-input-success';
      this.hasError = false;
      this.playNext();
    } else {
      this.inputStatus = 'minimal-input minimal-input-error';
      this.hasError = true;
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
      this.playAudio(this.currWord);
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

  onInputClick(): void {
    this.hasError = false;
    this.inputStatus = 'minimal-input';
  }

}
