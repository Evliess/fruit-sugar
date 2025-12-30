import { Component, computed, inject } from '@angular/core';
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

  // 1. 创建一个 Observable 流，只关注导航结束事件 (NavigationEnd)
  // 并映射出当前的 URL 字符串
  private currentUrl$ = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map((event: any) => event.urlAfterRedirects)
  );

  // 2. 将 Observable 转换为 Signal
  // initialValue 设为当前路由，防止页面刚刷新时信号为空
  currentUrl = toSignal(this.currentUrl$, { initialValue: this.router.url });

  // 3. 创建 Computed Signal (计算属性) 来决定是否显示按钮
  // 只要 currentUrl 发生变化，这个值会自动更新
  isShowTopMenu = computed(() => {
    const url = this.currentUrl();
    console.log("Current URL:", url);
    if (url && (url.includes('/word') || url.includes('/unknown-book')
      || url.includes('/wrong-book') || url.includes('user-summary'))) {
      return true;
    }
    return false;
  });

  isShowCIHUIMenu = computed(() => {
    const url = this.currentUrl();
    if (url.includes('/words')) {
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

  ngOnInit(): void {
    this.sugarDictService.getAllContentModules().subscribe({
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
    this.router.navigate(['/word'], { queryParams: { isCustom: isCustom, category: category } });
  }
  gotoKouYu(isCustom: boolean = false, moduleId: string) {
    if (!isCustom) this.router.navigate(['/sentence-item']);
    else this.router.navigate(['/kou-yu'], { queryParams: { isCustom: true, moduleId: moduleId } });
  }
  gotoListen() {
    const url = this.currentUrl();
    if (url.includes('moduleId=')) {
      const moduleId = url.split('moduleId=')[1];
      this.router.navigate(['/listen'], { queryParams: { moduleId: moduleId } });
    }
  }

  setLearnModel(model: string) {
    this.learnModel = model;
    if (model == 'learn') this.ciHuiService.setLearnMode(true);
    if (model == 'list') this.ciHuiService.setLearnMode(false);
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


}
