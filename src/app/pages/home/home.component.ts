import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';

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
  selector: 'app-home',
  imports: [CommonModule, NzGridModule, NzButtonModule, NzIconModule, NzCardModule],  
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor(private router: Router) { }
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
    },
    {
      name: '去听写',
      items: [
        { title: '运动追踪', href: '/word/1', desc: '记录步数，科学健身', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=800&q=80' },
        { title: '健康饮食', href: '/word/1', desc: '卡路里计算，营养搭配', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=800&q=80' },
        { title: '睡眠监测', href: '/word/1', desc: '改善睡眠，提升精力', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1512069772995-ec65ed45afd6?auto=format&fit=crop&w=800&q=80' }
      ]
    }
  ];

}
