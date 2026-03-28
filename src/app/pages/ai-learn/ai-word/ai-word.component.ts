import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

import { SugarDictService } from '../../../services/sugar-dict';
import { AuthService } from '../../../services/auth';

interface ChatMessage {
  id: string;
  userInput: string;
  aiResponse: string;
  timestamp: number;
}

@Component({
  selector: 'app-ai-word',
  imports: [CommonModule, FormsModule, NzInputModule, NzButtonModule, NzIconModule, NzCardModule, NzSpinModule, NzPopconfirmModule],
  templateUrl: './ai-word.component.html',
  styleUrl: './ai-word.component.css'
})
export class AiWordComponent implements OnInit {
  userInput = '';
  aiResponse = '';
  isLoading = false;
  chatMessages: ChatMessage[] = [];
  private readonly STORAGE_KEY = 'ai_word_chat_history';
  private readonly MAX_MESSAGES = 100;

  private sugarDictService = inject(SugarDictService);
  private authService = inject(AuthService);
  private message = inject(NzMessageService);
  currentUser = this.authService.currentUser;

  ngOnInit(): void {
    this.loadChatHistory();
  }

  private loadChatHistory(): void {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored) {
        this.chatMessages = JSON.parse(stored);
      }
    } catch (error) {
      console.error('加载聊天历史失败:', error);
      this.chatMessages = [];
    }
  }

  private saveChatHistory(): void {
    try {
      // 限制最多保存100条消息
      if (this.chatMessages.length > this.MAX_MESSAGES) {
        this.chatMessages = this.chatMessages.slice(-this.MAX_MESSAGES);
      }
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.chatMessages));
    } catch (error) {
      console.error('保存聊天历史失败:', error);
    }
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substring(2);
  }

  sendToAI(): void {
    if (!this.userInput.trim()) {
      return;
    }

    const userInput = this.userInput.trim();
    this.isLoading = true;

    // 创建临时消息用于显示加载状态
    const tempId = this.generateId();
    const tempMessage: ChatMessage = {
      id: tempId,
      userInput,
      aiResponse: '',
      timestamp: Date.now()
    };
    this.chatMessages.push(tempMessage);

    this.sugarDictService.getSmartWord(userInput, this.currentUser()?.id || -1).subscribe({
      next: (resp: any) => {
        const aiResponse = resp.data;
        this.aiResponse = aiResponse;

        // 更新临时消息
        const messageIndex = this.chatMessages.findIndex(msg => msg.id === tempId);
        if (messageIndex !== -1) {
          this.chatMessages[messageIndex].aiResponse = aiResponse;
        }

        this.isLoading = false;
        this.userInput = '';
        this.saveChatHistory();
        this.scrollToBottom();
      },
      error: (err: any) => {
        console.error('请求失败:', err);
        this.message.error('获取AI回复失败，请重试');

        // 移除临时消息
        this.chatMessages = this.chatMessages.filter(msg => msg.id !== tempId);

        this.isLoading = false;
      }
    });
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      const messagesContainer = document.querySelector('.chat-messages');
      if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
      }
    }, 100);
  }

  clearInput(): void {
    this.userInput = '';
  }

  clearChatHistory(): void {
    this.chatMessages = [];
    localStorage.removeItem(this.STORAGE_KEY);
    this.message.success('聊天历史已清空');
  }

  formatTimestamp(timestamp: number): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - timestamp;
    const diffMins = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMins < 1) {
      return '刚刚';
    } else if (diffMins < 60) {
      return `${diffMins}分钟前`;
    } else if (diffHours < 24) {
      return `${diffHours}小时前`;
    } else if (diffDays < 7) {
      return `${diffDays}天前`;
    } else {
      return date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
    }
  }

  renderMarkdown(content: string): string {
    if (!content) return '';
    const rawHtml = marked.parse(content) as string;
    return DOMPurify.sanitize(rawHtml);
  }
}
