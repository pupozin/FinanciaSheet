import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { API_URL } from '../app/api-url.token';

type AuthResponse = { token: string | null; name: string; email: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api = inject(API_URL);
  private tokenKey = 'auth_token';

  register(data: { name: string; email: string; password: string }) {
    return this.http
      .post<AuthResponse>(`${this.api}/auth/register`, data)
      .pipe(tap(res => this.saveToken(res.token)));
  }

  login(data: { email: string; password: string }) {
    return this.http
      .post<AuthResponse>(`${this.api}/auth/login`, data)
      .pipe(tap(res => this.saveToken(res.token)));
  }

  private storageAvailable(): boolean {
    return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
  }

  saveToken(token: string | null): void {
    if (!this.storageAvailable()) return;
    if (token) {
      window.localStorage.setItem(this.tokenKey, token);
    } else {
      window.localStorage.removeItem(this.tokenKey);
    }
  }

  getToken(): string | null {
    return this.storageAvailable() ? window.localStorage.getItem(this.tokenKey) : null;
  }

  logout(): void {
    if (this.storageAvailable()) {
      window.localStorage.removeItem(this.tokenKey);
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
