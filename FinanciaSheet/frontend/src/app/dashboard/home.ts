import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsApiService, Overview, CategoryRow, DailyPoint, MerchantRow } from '../core/analytics.api';
import { MonthSelectComponent } from './month-select';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, MonthSelectComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent {
  private api = inject(AnalyticsApiService);

  // período selecionado (opcional; se vazio, backend usa "último mês com dados")
  from = signal<string | null>(null);
  to   = signal<string | null>(null);

  // dados
  loading = signal(true);
  ov = signal<Overview | null>(null);
  topCats = signal<CategoryRow[]>([]);
  topMerch = signal<MerchantRow[]>([]);
  daily = signal<DailyPoint[]>([]);
  error = signal<string | null>(null);

  // formato período para título
  periodLabel = computed(() => {
    if (!this.from() || !this.to()) return 'Último mês com dados';
    const d = new Date(this.from() + 'T00:00:00');
    const mes = d.toLocaleString('pt-BR', { month: 'long', year: 'numeric', timeZone: 'UTC' });
    return mes.charAt(0).toUpperCase() + mes.slice(1);
  });

  ngOnInit(){ this.load(); }

  onRangeChange(range: {from:string,to:string}){
    this.from.set(range.from);
    this.to.set(range.to);
    this.load();
  }

  private load(){
    this.loading.set(true); this.error.set(null);
    const from = this.from() ?? undefined;
    const to   = this.to()   ?? undefined;

    // paraleliza simples: dispara e cada um seta seu estado
    this.api.overview(from, to).subscribe({
      next: d => this.ov.set(d),
      error: e => this.error.set(e?.error?.message || 'Erro no overview')
    });

    this.api.topCategories(from, to, 10).subscribe({
      next: d => this.topCats.set(d),
      error: e => this.error.set(e?.error?.message || 'Erro em top categorias')
    });

    this.api.topMerchants(from, to, 10).subscribe({
      next: d => this.topMerch.set(d),
      error: e => this.error.set(e?.error?.message || 'Erro em top merchants')
    });

    this.api.daily(from, to).subscribe({
      next: d => { this.daily.set(d); this.loading.set(false); },
      error: e => { this.error.set(e?.error?.message || 'Erro em série diária'); this.loading.set(false); }
    });
  }
}
