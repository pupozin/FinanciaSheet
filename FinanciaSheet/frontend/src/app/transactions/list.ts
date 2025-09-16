import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, Transaction } from '../core/api';

@Component({
  selector: 'app-transactions-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list.html',
  styleUrls: ['./list.scss']
})
export class ListComponent {
  private api = inject(ApiService);
  loading = signal(false);
  page = signal(0);
  size = signal(20);
  total = signal(0);
  items = signal<Transaction[]>([]);
  error = signal<string | null>(null);

  ngOnInit(){ this.load(); }

  load(page = 0){
    this.loading.set(true); this.error.set(null);
    this.api.listTransactions(page, this.size()).subscribe({
      next: p => { this.items.set(p.content); this.total.set(p.totalElements); this.page.set(p.number); this.loading.set(false); },
      error: e => { this.error.set(e?.error?.message || 'Erro ao listar'); this.loading.set(false); }
    });
  }
}
