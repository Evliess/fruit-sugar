import { Directive, EventEmitter, HostListener, Input, OnDestroy, Output } from '@angular/core';
import { Subject, timer, EMPTY, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Directive({
    selector: '[appHoverSound]'
})

export class HoverSoundDirective implements OnDestroy {
    @Input() hoverTime = 500;
    @Output() hoverTimeout = new EventEmitter<void>();

    private hoverState$ = new Subject<boolean>();
    private sub: Subscription;

    constructor() {
        this.sub = this.hoverState$.pipe(
            switchMap(isHover => isHover ? timer(this.hoverTime) : EMPTY)
        ).subscribe(() => {
            this.hoverTimeout.emit();
        });
    }

    @HostListener('mouseenter') onMouseEnter() {
        this.hoverState$.next(true);
    }

    @HostListener('mouseleave') onMouseLeave() {
        this.hoverState$.next(false);
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }
}