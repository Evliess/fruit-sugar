import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';

import { Router } from '@angular/router';

interface CardItem {
  title: string;
  desc: string;
  href: string;
  bgImage: string;
  progress?: string;
}

interface Section {
  name: string;
  items: CardItem[];
}

@Component({
  selector: 'app-user-summary',
  imports: [CommonModule, NzGridModule, NzProgressModule, NzToolTipModule, NzCardModule],  
  templateUrl: './user-summary.component.html',
  styleUrl: './user-summary.component.css'
})
export class UserSummaryComponent {

    sections: Section[] = [
    {
      name: '场景词汇',
      items: [
        { title: '学习与学校', href: '/word/1', desc: '下午帮我签到', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80' },
        { title: '食物', href: '/word/1', desc: '来呗奶茶吧', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
        { title: '公共交通', href: '/word/1', desc: '地铁公交，一码通行', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
        { title: '住宅', href: '/word/1', desc: '满城尽带黄金甲', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=800&q=80' }
      ]
    },
    {
      name: '口语表达',
      items: [
        { title: '学习与学校', href: '/listen', desc: '下午帮我签到', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1501504905252-473c47e087f8?auto=format&fit=crop&w=800&q=80' },
        { title: '食物', href: '/listen', desc: '小饿小困，就喝香飘飘', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=800&q=80' },
        { title: '公共交通', href: '/listen', desc: '地铁公交，一码通行', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=80' }
      ]
    }
  ];

}
