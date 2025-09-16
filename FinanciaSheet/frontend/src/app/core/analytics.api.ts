import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../app/api-url.token';
import { Observable } from 'rxjs';

export type Overview = { received: number; spent: number; balance: number; txCount: number };
export type CategoryRow = { category: string; total: number };
export type DailyPoint = { date: string; received: number; spent: number; balance: number };
export type MonthlyCashflow = { month: string; received: number; spent: number; balance: number };
export type MerchantRow = { description: string; total: number; count: number };

@Injectable({ providedIn: 'root' })
export class AnalyticsApiService {
  private http = inject(HttpClient);
  private api  = inject(API_URL);

  private params(from?: string, to?: string, extra?: Record<string, string|number>) {
    let p = new HttpParams();
    if (from) p = p.set('from', from);
    if (to)   p = p.set('to', to);
    if (extra) for (const [k,v] of Object.entries(extra)) p = p.set(k, String(v));
    return p;
  }

  months(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/analytics/months`);
  }

  overview(from?: string, to?: string): Observable<Overview> {
    return this.http.get<Overview>(`${this.api}/analytics/overview`, { params: this.params(from, to) });
  }

  topCategories(from?: string, to?: string, limit = 10): Observable<CategoryRow[]> {
    return this.http.get<CategoryRow[]>(`${this.api}/analytics/categories/top`, { params: this.params(from, to, { limit }) });
  }

  daily(from?: string, to?: string): Observable<DailyPoint[]> {
    return this.http.get<DailyPoint[]>(`${this.api}/analytics/daily`, { params: this.params(from, to) });
  }

  monthly(months = 6): Observable<MonthlyCashflow[]> {
    return this.http.get<MonthlyCashflow[]>(`${this.api}/analytics/cashflow/monthly`, { params: new HttpParams().set('months', months) });
  }

  topMerchants(from?: string, to?: string, limit = 10): Observable<MerchantRow[]> {
    return this.http.get<MerchantRow[]>(`${this.api}/analytics/merchants/top`, { params: this.params(from, to, { limit }) });
  }
}
