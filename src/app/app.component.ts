import { Component, computed, inject } from '@angular/core';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd} from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiHuiService } from './pages/ci-hui/ci-hui.service';


@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, NzIconModule, NzDropDownModule, NzLayoutModule, NzMenuModule, NzButtonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  isCollapsed = false;
  learnModel = "list";

  private router = inject(Router);
  private showAllState = inject(CiHuiService)

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
    
    // 逻辑：如果当前 URL 是 '/order'，则隐藏；其他情况显示
    // 你可以根据需求修改这里的逻辑，比如使用 includes()
    if (url && url.includes('/home')) {
      return false;
    }
    
    // 只有在 home 或者其他页面才显示 (根据你的需求调整)
    return true; 
  });

  ngOnInit(): void {
  }

  gotoWord() { this.router.navigate(['/word']); }
  gotoListen() { this.router.navigate(['/listen']); }
  setLearnModel(model: string) {
    this.learnModel = model;
    if (model == 'learn') this.showAllState.setShowAll(true);
    if (model == 'list') this.showAllState.setShowAll(false);
  }
}
