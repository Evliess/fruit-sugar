import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Ant Design V19 Imports
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

import { trigger, style, animate, transition } from '@angular/animations';
import { MechanicalTypingDirective } from '../../mechanical-typing.directive';




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
    NzTagModule
  ],
  templateUrl: './listen-write.component.html',
  styleUrl: './listen-write.component.css',
  animations: [
    trigger('switchAnimation', [
      // 1. 进场动画 (Enter): 渐渐浮现
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),

      // 2. 离场动画 (Leave): 瞬间消失
      transition(':leave', [
        animate('0ms', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class ListenWriteComponent {
  inputValue: string = '';
  isEyeOpen: boolean = false;
  isPlay: boolean = true;
  soundEnabled = true;


  // 模拟点击事件
  handleAction(action: string): void {
    console.log('Clicked:', action);
    if("play"==action) {this.togglePlay()};
  }

  togglePlay(): void {
    this.isPlay = !this.isPlay;
  }

  toggleEye(): void {
    this.isEyeOpen = !this.isEyeOpen;
    // 这里可以加入实际的业务逻辑，例如隐藏中文释义等
    console.log(this.isEyeOpen ? 'Mode: Visible' : 'Mode: Hidden');
  }

}
