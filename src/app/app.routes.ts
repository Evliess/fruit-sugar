import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/home' },
  { path: 'home', loadChildren: () => import('./pages/home/home.routes').then(m => m.HOME_ROUTES) },
  { path: 'listen', loadChildren: () => import('./pages/listen-write/listen-write.routes').then(m => m.LISTEN_WRITE_ROUTES) },
  { path: 'words', loadChildren: () => import('./pages/ci-hui/ci-hui.routes').then(m => m.CI_HUI_ROUTES) },
  { path: 'user-summary', loadChildren: () => import('./pages/user-summary/user-summary.routes').then(m => m.USER_SUMMARY_ROUTES) },
  { path: 'kou-yu', loadChildren: () => import('./pages/kou-yu/kou-yu.routes').then(m => m.KOU_YU_ROUTES)},
  { path: 'unknown-book', loadChildren: () => import('./pages/unknown-book/unknown-book.routes').then(m => m.UNKNOWN_BOOK_ROUTES) },
  { path: 'wrong-book', loadChildren: () => import('./pages/wrong-book/wrong-book.routes').then(m => m.WRONG_BOOK_ROUTES) },
  { path: 'word-subitem', loadChildren: () => import('./pages/word-subitem/word-subitem.routes').then(m => m.WORD_SUBITEM_ROUTES) },
  { path: 'sentence-item', loadChildren: () => import('./pages/sentence-item/sentence-item.routes').then(m => m.SENTENCE_ITEM_ROUTES) },
];
