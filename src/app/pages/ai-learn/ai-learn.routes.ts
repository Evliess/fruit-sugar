import { Routes } from '@angular/router';

export const AI_LEARN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/ai-learn-home.component').then(m => m.AiLearnHomeComponent)
  },
  {
    path: 'ai-word',
    loadComponent: () => import('./ai-word/ai-word.component').then(m => m.AiWordComponent)
  },
  {
    path: 'sentence',
    loadComponent: () => import('./sentence/ai-sentence.component').then(m => m.AiSentenceComponent)
  }
];
