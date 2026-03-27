import { Injectable, signal, computed } from '@angular/core';

export interface User {
  id: number;
  name: string;
  code: string;
  role: 'admin' | 'user';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSignal = signal<User | null>(this.getUserFromStorage());
  currentUser = this.userSignal.asReadonly();
  isAuthenticated = computed(() => !!this.userSignal());
  isAdmin = computed(() => this.userSignal()?.role === 'admin');
  isUser = computed(() => this.userSignal()?.role === 'user');
  
  private getUserFromStorage(): any {
    const data = localStorage.getItem('auth_user');
    if (!data) return null;
    try {
      return JSON.parse(data);
    } catch {
      return null;
    }
  }

  setSession(user: User) {
    this.userSignal.set(user);
    localStorage.setItem('auth_user', JSON.stringify(user));
  }

  loadSavedSession() {
    const savedUser = localStorage.getItem('auth_user');
    if (savedUser) {
      try {
        this.userSignal.set(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('auth_user');
      }
    }
  }

  logout() {
    this.userSignal.set(null);
    localStorage.removeItem('auth_user');
  }

  initializeAuth() {
    const savedUser = localStorage.getItem('auth_user');
    if (savedUser) {
      this.userSignal.set(JSON.parse(savedUser));
    }
  }
}