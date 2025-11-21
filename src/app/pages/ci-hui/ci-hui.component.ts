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

import { trigger, state, style, animate, transition } from '@angular/animations';



@Component({
  selector: 'app-ci-hui',
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
    NzTagModule
  ],
  templateUrl: './ci-hui.component.html',
  styleUrl: './ci-hui.component.css',
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
export class CiHuiComponent {
  inputValue: string = '';
  isEyeOpen: boolean = false;

  // 模拟点击事件
  handleAction(action: string): void {
    console.log('Clicked:', action);
  }

  onSwitchChange(checked: boolean): void {
    console.log(checked ? '标记为：认识' : '标记为：不认识');
  }

  toggleEye(): void {
    this.isEyeOpen = !this.isEyeOpen;
    // 这里可以加入实际的业务逻辑，例如隐藏中文释义等
    console.log(this.isEyeOpen ? 'Mode: Visible' : 'Mode: Hidden');
  }

}
