import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzIconModule } from 'ng-zorro-antd/icon';

@Component({
  selector: 'app-ai-learn-home',
  imports: [NzGridModule, NzIconModule],
  templateUrl: './ai-learn-home.component.html',
  styleUrl: './ai-learn-home.component.css'
})
export class AiLearnHomeComponent {
  constructor(private router: Router) {}

  navigateToAiWord(): void {
    this.router.navigate(['/ai-learn/ai-word']);
  }

  navigateToAiSentence(): void {
    this.router.navigate(['/ai-learn/sentence']);
  }
}
