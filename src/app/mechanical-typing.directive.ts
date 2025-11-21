import { Directive, HostListener, Input } from '@angular/core';

@Directive({
  selector: '[appMechanicalTyping]',
  standalone: true
})
export class MechanicalTypingDirective {
  private audio: HTMLAudioElement | null = null;
  private lastPlayTime = 0;
  private readonly DEBOUNCE_MS = 80;

  @Input() soundUrl = 'assets/sounds/keyclick.mp3';
  @Input('appMechanicalTyping') enableSound= true;

  constructor() {
    this.audio = new Audio();
    this.audio.src = this.soundUrl;
    this.audio.load();
  }

  @HostListener('keydown', ['$event'])
  onKeydown(event: KeyboardEvent) {
    if (!this.enableSound) return;

    if (
      event.ctrlKey ||
      event.altKey ||
      event.metaKey ||
      ['Tab', 'Escape', 'ArrowLeft', 'ArrowRight'].includes(event.key)
    ) {
      return;
    }

    const now = Date.now();
    if (now - this.lastPlayTime > this.DEBOUNCE_MS) {
      this.playSound();
      this.lastPlayTime = now;
    }
  }

  private playSound() {
    if (!this.audio) return;
    const clone = this.audio.cloneNode() as HTMLAudioElement;
    clone.volume = 0.3;
    clone.play().catch(e => console.warn('Audio play failed:', e));
  }
}