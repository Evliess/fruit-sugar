// auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 检查 AuthService 里的 Signal 是否有值
  const isAuth = authService.isAuthenticated();

  if (isAuth) {
    return true;
  }

  // 如果没登录，重定向到登录页
  return router.createUrlTree(['/login']);
};