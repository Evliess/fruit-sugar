import { Component, OnDestroy, OnInit, Input  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzSwitchModule } from 'ng-zorro-antd/switch';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { NzPopoverModule } from 'ng-zorro-antd/popover';


import { MechanicalTypingDirective } from '../../mechanical-typing.directive';

export interface Track {
  title: string;
  artist: string;
  src: string;
}

@Component({
  selector: 'app-music-player',
  templateUrl: './music-player.component.html',
  styleUrls: ['./music-player.component.css'],
  imports: [NzIconModule,NzButtonModule, 
    NzSwitchModule,FormsModule,NzCardModule, 
    NzInputModule, MechanicalTypingDirective
    ,NzSpaceModule, NzPopoverModule],
})
export class MusicPlayerComponent implements OnInit, OnDestroy {
  isPlaying = false;
  currentTrackIndex = 0;
  userInput = '';
  statusClass = '';
  soundEnabled = true;
  color1 = 'blue';
  
  @Input() playlist: Track[] = [];
  private audio = new Audio();

  ngOnInit(): void {
    this.loadCurrentTrack();
    this.audio.addEventListener('ended', () => {
      this.playNext();
    });
    // Start playing automatically for demo purposes
    // this.audio.play().catch(e => console.warn('Playback failed:', e));
    // this.isPlaying = !this.isPlaying;
  }

  ngOnDestroy(): void {
    this.audio.pause();
    this.audio.removeEventListener('ended', () => { });
  }

  loadCurrentTrack(): void {
    this.audio.src = this.currentTrack.src;
    this.audio.load();
  }

  togglePlayPause(): void {
    if (this.isPlaying) {
      this.audio.pause();
    } else {
      this.audio.play().catch(e => console.warn('Playback failed:', e));
    }
    this.isPlaying = !this.isPlaying;
  }

  playPrev(): void {
    this.currentTrackIndex =
      (this.currentTrackIndex - 1 + this.playlist.length) % this.playlist.length;
    this.loadCurrentTrack();
    this.audio.play().then(() => {
      this.isPlaying = true;
    }).catch(e => console.warn('Playback failed:', e));
  }

  playNext(): void {
    this.currentTrackIndex = (this.currentTrackIndex + 1) % this.playlist.length;
    this.loadCurrentTrack();
    this.audio.play().then(() => {
      this.isPlaying = true;
    }).catch(e => console.warn('Playback failed:', e));
  }

  get currentTrack() {
    return this.playlist[this.currentTrackIndex];
  }

  onInput(event: KeyboardEvent) {
    const input = event.target as HTMLInputElement;
    if(input.value.trim() !== 'hello'){
      this.statusClass = '';
    }
  }
  
  checkValue(): void {
    if (this.userInput.trim() === 'hello') {
      this.statusClass = 'ant-input-status-success';
    } else {
      this.statusClass = 'ant-input-status-error';
    }
  }
}