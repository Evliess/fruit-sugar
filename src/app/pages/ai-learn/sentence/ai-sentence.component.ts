import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzMessageService } from 'ng-zorro-antd/message';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

import { SugarDictService } from '../../../services/sugar-dict';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-ai-sentence',
  imports: [CommonModule, FormsModule, NzInputModule, NzButtonModule, NzIconModule, NzCardModule, NzSpinModule],
  templateUrl: './ai-sentence.component.html',
  styleUrl: './ai-sentence.component.css'
})
export class AiSentenceComponent {
  userInput = '';
  aiResponse = '';
  isLoading = false;

  private sugarDictService = inject(SugarDictService);
  private authService = inject(AuthService);
  private message = inject(NzMessageService);
  currentUser = this.authService.currentUser;

  sendToAI(): void {
    if (!this.userInput.trim()) {
      return;
    }

    this.isLoading = true;
    
    this.sugarDictService.getSmartSentence(this.userInput, this.currentUser()?.id || -1).subscribe({
      next: (response: any) => {
        this.aiResponse = response;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('请求失败:', err);
        this.message.error('获取AI回复失败，请重试');
        this.isLoading = false;
      }
    });
  }

  clearInput(): void {
    this.userInput = '';
    this.aiResponse = '';
  }

  renderMarkdown(content: string): string {
    if (!content) return '';
    const rawHtml = marked.parse(content) as string;
    return DOMPurify.sanitize(rawHtml);
  }
}
