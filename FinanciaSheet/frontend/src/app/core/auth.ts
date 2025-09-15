// src/app/core/auth.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../app/api-url.token';  // ✅ CORRETO (sem ../app/)
import { tap } from 'rxjs/operators';

type AuthResponse = { token: string | null; name: string; email: string };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api  = inject(API_URL);
  private tokenKey = 'auth_token';

  register(data: { name: string; email: string; password: string }) {
    return this.http.post<AuthResponse>(`${this.api}/auth/register`, data)
      .pipe(tap(res => this.saveToken(res.token)));
  }
  login(data: { email: string; password: string }) {
    return this.http.post<AuthResponse>(`${this.api}/auth/login`, data)
      .pipe(tap(res => this.saveToken(res.token)));
  }

  saveToken(token: string | null){ if (token) localStorage.setItem(this.tokenKey, token); }
  getToken(){ return localStorage.getItem(this.tokenKey); }
  logout(){ localStorage.removeItem(this.tokenKey); }
  isLoggedIn(){ return !!this.getToken(); }
}
