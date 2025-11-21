import { Component } from '@angular/core';
import {MusicPlayerComponent} from "../music-player/music-player.component";
import { Track } from '../music-player/music-player.component';
import { ActivatedRoute } from '@angular/router';



@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css',
  imports: [MusicPlayerComponent]
})
export class WelcomeComponent {
  constructor(private route: ActivatedRoute) {}
  id!: string;

  homeTracks: Track[] = [
    { title: 'Sunset Vibes', artist: 'Artist A', src: 'https://dict.youdao.com/dictvoice?audio=brilliance&type=1' },
    { title: 'Midnight Drive', artist: 'Artist B', src: 'https://dict.youdao.com/dictvoice?audio=hello&type=1' },
    { title: 'Ocean Waves', artist: 'Artist C', src: 'https://dict.youdao.com/dictvoice?audio=world&type=1' }
  ];


  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') || '';
    console.log('WelcomeComponent initialized with id:', this.id);
  }

}
