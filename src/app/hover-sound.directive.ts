import { Directive, EventEmitter, HostListener, Input, OnDestroy, Output } from '@angular/core';
import { Subject, timer, EMPTY, Subscription } from 'rxjs';
import { switchMap, distinctUntilChanged } from 'rxjs/operators';

@Directive({
    selector: '[appHoverSound]',
    standalone: true
})
export class HoverSoundDirective implements OnDestroy {
    @Input() hoverTime = 500;
    @Output() hoverTimeout = new EventEmitter<void>();
    @Output() soundClick = new EventEmitter<void>();

    // 使用 Subject 管理状态流
    private hoverState$ = new Subject<boolean>();
    private sub: Subscription;

    constructor() {
        this.sub = this.hoverState$.pipe(
            // 1. 只有当状态真正改变时才处理 (防止重复的 mouseenter)
            distinctUntilChanged(),
            // 2. switchMap 的特性：当新值到来时，如果旧的 timer 还没跑完，会直接杀掉旧的
            switchMap(isHover => {
                if (isHover) {
                    return timer(this.hoverTime);
                } else {
                    // 移出时返回 EMPTY，这会立即取消上一个还在跑的 timer
                    return EMPTY;
                }
            })
        ).subscribe(() => {
            this.hoverTimeout.emit();
        });
    }

    @HostListener('mouseenter') 
    onMouseEnter() {
        this.hoverState$.next(true);
    }

    @HostListener('mouseleave') 
    onMouseLeave() {
        this.hoverState$.next(false);
    }

    @HostListener('click') 
    onClick() {
        // 3. 点击时立即发送 false，强制取消所有悬停计时
        this.hoverState$.next(false);
        this.soundClick.emit();
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }
}