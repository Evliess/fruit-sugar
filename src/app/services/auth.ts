import { Injectable, signal, computed } from '@angular/core';

export interface User {
  id: string;
  name: string;
  role: 'admin' | 'user';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSignal = signal<User | null>(null);

  currentUser = this.userSignal.asReadonly();
  isAuthenticated = computed(() => !!this.userSignal());

  setSession(user: User) {
    this.userSignal.set(user);
    sessionStorage.setItem('user_profile', JSON.stringify(user));
  }

  initializeAuth() {
    const savedUser = sessionStorage.getItem('user_profile');
    if (savedUser) {
      this.userSignal.set(JSON.parse(savedUser));
    }
  }
}