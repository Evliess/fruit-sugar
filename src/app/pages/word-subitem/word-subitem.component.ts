import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
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
  selector: 'app-word-subitem',
  imports: [CommonModule, NzGridModule, NzCardModule],  
  templateUrl: './word-subitem.component.html',
  styleUrl: './word-subitem.component.css'
})
export class WordSubitemComponent {

  sections: Section[] = [
    {
      name: '学习与学校',
      items: [
        { title: '学习与学校', href: '/word/1', desc: '下午帮我签到', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80' },
        { title: '食物', href: '/word/1', desc: '来呗奶茶吧', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
        { title: '公共交通', href: '/word/1', desc: '地铁公交，一码通行', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80' },
        { title: '住宅', href: '/word/1', desc: '满城尽带黄金甲', progress: '3/56', bgImage: 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=800&q=80' }
      ]
    },
  ];

}
