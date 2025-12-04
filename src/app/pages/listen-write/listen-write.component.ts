import { Component } from '@angular/core';
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

import { MechanicalTypingDirective } from '../../mechanical-typing.directive';


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
  isEyeOpen: boolean = false;
  isPlay: boolean = true;
  soundEnabled: boolean = true;
  soundTypeUK: boolean = true;

  words: VocabularyWord[] = [
    {
      id: 1, word: 'hello', definitions: [{ type: 'int.', zh: '喂，你好（用于问候或打招呼)' },
      { type: 'n.', zh: '招呼，问候' },
      { type: 'v.', zh: '说（或大声说）“喂”' }],
      usAudio: 'https://dict.youdao.com/dictvoice?audio=hello&type=1',
      ukAudio: 'https://dict.youdao.com/dictvoice?audio=hello&type=2'
    },
    {
      id: 2, word: 'world', definitions: [{ type: 'n.', zh: '世界，地球，天下' },
      { type: 'adj.', zh: '世界上最重要的，世界闻名的' },
      ],
      usAudio: 'https://dict.youdao.com/dictvoice?audio=world&type=1',
      ukAudio: 'https://dict.youdao.com/dictvoice?audio=world&type=2'
    },
    {
      id: 3, word: 'rich', definitions: [{ type: 'adj.', zh: '有钱的，富有的' },
      ],
      usAudio: 'https://dict.youdao.com/dictvoice?audio=rich&type=1',
      ukAudio: 'https://dict.youdao.com/dictvoice?audio=rich&type=2'
    },
  ];
  currIndex: number = 0;
  currWord: VocabularyWord = this.words[this.currIndex];

  private audio = new Audio();

  ngOnInit(): void {
    this.playAudio();
  }

  playAudio(): void {
    this.audio.src = this.soundTypeUK ? this.currWord.ukAudio : this.currWord.usAudio;
    this.audio.load();
    this.audio.play().catch(e => console.warn('Playback failed:', e));
  }

  playNext(): void {
    this.currIndex = (this.currIndex + 1) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.playAudio();
  }

  playPrev(): void {
    this.currIndex = (this.currIndex - 1 + this.words.length) % this.words.length;
    this.currWord = this.words[this.currIndex];
    this.inputValue = '';
    this.playAudio();
  }



  // 模拟点击事件
  handleAction(action: string): void {
    console.log('Clicked:', action);
    if ("play" == action) {
      this.togglePlay();
      this.playAudio();
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
