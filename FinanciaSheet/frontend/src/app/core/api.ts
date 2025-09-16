import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../app/api-url.token';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

export type ImportResponse = {
  batchId: string;
  imported: number;
  duplicates: number;
  errors: number;
};

export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

export type Transaction = {
  id: string;
  date: string;       // ISO
  amount: number;
  description: string;
  category?: string;
  account?: string;
  kind?: string;      // EXTRATO|FATURA
  source?: string;    // nubank_csv|xp_csv...
};

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private api  = inject(API_URL);
  private auth = inject(AuthService);

  private authHeaders(): Record<string, string> | undefined {
    const token = this.auth.getToken();
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  uploadCsv(kind: 'EXTRATO'|'FATURA', file: File, source = 'unknown_csv', account?: string): Observable<ImportResponse> {
    const form = new FormData();
    form.append('file', file);

    let params = new HttpParams()
      .set('kind', kind)
      .set('source', source);
    if (account) params = params.set('account', account);

    return this.http.post<ImportResponse>(`${this.api}/transactions/import`, form, {
      params,
      headers: this.authHeaders(),
      withCredentials: false,
    });
  }

  listTransactions(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Transaction>>(`${this.api}/transactions`, {
      params,
      headers: this.authHeaders(),
      withCredentials: false,
    });
  }
}
