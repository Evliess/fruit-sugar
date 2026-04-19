import { Component, computed, inject, effect } from '@angular/core';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiHuiService } from '../../pages/ci-hui/ci-hui.service';
import { SugarDictService } from '../../services/sugar-dict';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-main-layout',
  imports: [NzIconModule, NzLayoutModule, NzMenuModule, NzCardModule, NzDropDownModule, CommonModule, RouterOutlet],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  isCollapsed = false;
  learnModel = "list";
  category = "";

  sentenceCases: any[] = []
  wordCases: any[] = []


  private router = inject(Router);
  private ciHuiService = inject(CiHuiService)
  private sugarDictService = inject(SugarDictService)
  private authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  // 1. 创建一个 Observable 流，只关注导航结束事件 (NavigationEnd)
  // 并映射出当前的 URL 字符串
  private currentUrl$ = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map((event: any) => event.urlAfterRedirects)
  );

  // 2. 将 Observable 转换为 Signal
  // initialValue 设为当前路由，防止页面刚刷新时信号为空
  currentUrl = toSignal(this.currentUrl$, { initialValue: this.router.url });

  isShowTopMenu = computed(() => {
    const url = this.currentUrl();
    if (url && ((url.includes('/words') && !url.includes('isCustom=true')) || url.includes('/unknown-book')
      || url.includes('/wrong-book') || url.includes('user-summary')
      || url.includes('/kou-yu') || url.includes('/listen') || url.includes('/sen-listen')
      || url.includes('/word-subitem') || url.includes('/sentence-item') || url.includes('/ai-learn')
    )) {
      return true;
    }
    return false;
  });

  isRequiredToCloseLeftMenuPage = computed(() => {
    const url = this.currentUrl();
    return url && (url.includes('/listen')
      || url.includes('/words') 
      || url.includes('/kou-yu')
      || url.includes('/ai-word')
      || url.includes('/ai-sentence')
      || url.includes('user-summary')
      
    );
  });

  constructor() {
    effect(() => {
      const url = this.currentUrl();
      if (this.isRequiredToCloseLeftMenuPage()) {
        this.isCollapsed = true;
      }

      if (url.includes('/listen')) {
        this.learnModel = 'listen';
      } else if (url.includes('/words/list1')) {
        this.learnModel = 'list1';
      } else if (url.includes('/words/learn')) {
        this.learnModel = 'learn';
      } else if (url.includes('/words')) {
        this.learnModel = 'list';
      } else if (url.includes('/kou-yu?isCustom=false')) {
        this.learnModel = 'learn';
      } else if (url.includes('/sen-listen')) {
        this.learnModel = 'listen';
      } else if (url.includes('/wrong-book-listen')) {
        this.learnModel = 'wrong-listen';
      }
    });
  }

  isShowCIHUIMenu = computed(() => {
    const url = this.currentUrl();
    if (url.includes('/words') || url.includes('/listen')) {
      return true;
    } else {
      return false;
    }
  });

  isShowSentenceMenu = computed(() => {
    const url = this.currentUrl();
    if (url.includes('/kou-yu') || url.includes('/sen-listen')) {
      return true;
    } else {
      return false;
    }
  });

  isShowUserMenu = computed(() => {
    const url = this.currentUrl();
    if (url.includes('/user-summary') || url.includes('/unknown-book') || url.includes('/wrong-book')) {
      return true;
    } else {
      return false;
    }
  });

  isUserCenterActive = computed(() => {
    const url = this.currentUrl();
    return url.includes('/user-summary') || url.includes('/unknown-book') || url.includes('/wrong-book');
  });

  isShowWordSubitemMenu = computed(() => {
    const url = this.currentUrl();
    return url.includes('/word-subitem');
  });

  // word-subitem 分类列表
  wordSubitemCategories = [
    { name: '学习与学校', parentName: 'school', parentDescription: '学习与学校 (Campus Life & Study)' },
    { name: '食物', parentName: 'food', parentDescription: '食物 (Food)' },
    { name: '住宅', parentName: 'housing', parentDescription: '住宅 (Housing)' },
    { name: '购物', parentName: 'shopping', parentDescription: '购物 (Shopping)' },
    { name: '休闲娱乐', parentName: 'leisure', parentDescription: '休闲娱乐 (Recreation & Leisure)' },
    { name: '城市与交通', parentName: 'city', parentDescription: '城市与交通 (City & Transportation)' },
    { name: '健康', parentName: 'health', parentDescription: '健康 (Health)' }
  ];

  isShowSentenceItemMenu = computed(() => {
    const url = this.currentUrl();
    return url.includes('/sentence-item');
  });

  isShowAiLearnMenu = computed(() => {
    const url = this.currentUrl();
    return url.includes('/ai-learn');
  });

  ngOnInit(): void {
    this.sugarDictService.getSentenceContentModules(this.currentUser()?.id || -1).subscribe({
      next: (response: any) => {
        this.sentenceCases = response.sentenceCases;
        this.wordCases = response.wordCases;
      },
      error: (err) => console.error('请求失败:', err)
    });
  }

  getChineseContentModuleName(name: string) {
    return this.sugarDictService.getChineseContentModuleName(name);
  }
  getEnglishContentModuleName(name: string) {
    return this.sugarDictService.getEnglishContentModuleName(name);
  }

  gotoWordSubitem(type: string, isCustom: boolean = false, parentName: string, parentDescription: string) {
    this.router.navigate(['/word-subitem'],
      { queryParams: { type: type, isCustom: isCustom, parentName: parentName, parentDescription: parentDescription } });
  }

  gotoWord(isCustom: boolean = false, category: string) {
    this.category = category;
    this.router.navigate(['/words'], { queryParams: { isCustom: isCustom, category: category } });
  }
  gotoKouYu(isCustom: boolean = false, moduleName?: string) {
    if (!isCustom) this.router.navigate(['/sentence-item']);
    else this.router.navigate(['/kou-yu'], { queryParams: { isCustom: true, moduleName: moduleName } });
  }
  gotoListen() {
    const url = this.currentUrl();
    const tree = this.router.parseUrl(url);
    const moduleId = tree.queryParams['moduleId'];
    if (moduleId) {
      this.router.navigate(['/listen'], { queryParams: { moduleId: moduleId } });
    }
  }

  gotoSentenceListen() {
    const url = this.currentUrl();
    const tree = this.router.parseUrl(url);
    const moduleId = tree.queryParams['moduleId'];
    if (moduleId) {
      this.learnModel = 'listen';
      this.router.navigate(['/sen-listen'], { queryParams: { moduleId: moduleId } });
    }
  }
  gotoSentenceLearn() {
    const url = this.currentUrl();
    const tree = this.router.parseUrl(url);
    const moduleId = tree.queryParams['moduleId'];
    if (moduleId) {
      this.learnModel = 'learn';
      this.router.navigate(['/kou-yu'], { queryParams: { moduleId: moduleId, isCustom: false } });
    }
  }

  setLearnModel(model: string) {
    const url = this.currentUrl();
    const tree = this.router.parseUrl(url);
    const queryParams = tree.queryParams;

    if (model === 'learn') {
      this.router.navigate(['/words/learn'], { queryParams });
    } else if (model === 'list') {
      this.router.navigate(['/words'], { queryParams });
    } else if (model === 'list1') {
      this.router.navigate(['/words/list1'], { queryParams });
    }
    this.learnModel = model;
  }

  gotoUserSummary() {
    this.router.navigate(['/user-summary']);
    this.category = "user-summary";
  }

  gotoUnknownBook() {
    this.router.navigate(['/unknown-book']);
    this.category = "unknown-book";
  }

  gotoWrongBook() {
    this.router.navigate(['/wrong-book']);
    this.category = "wrong-book";
  }

  gotoAiLearn() {
    this.router.navigate(['/ai-learn']);
    this.category = "ai-learn";
  }

  gotoAiWord() {
    this.router.navigate(['/ai-learn/ai-word']);
    this.category = "ai-learn";
  }

  gotoAiSentence() {
    this.router.navigate(['/ai-learn/sentence']);
    this.category = "ai-learn";
  }

  onAiLearnOpenChange(isOpen: boolean): void {
    if (isOpen) {
      this.gotoAiLearn();
    }
  }

  gotoWrongBookListen(): void {
    this.learnModel = 'wrong-listen';
    this.router.navigate(['/wrong-book-listen']);
  }

  gotoWordSubitemCategory(parentName: string, parentDescription: string): void {
    this.router.navigate(['/word-subitem'],
      { queryParams: { type: 'words', isCustom: false, parentName: parentName, parentDescription: parentDescription } });
  }


}
