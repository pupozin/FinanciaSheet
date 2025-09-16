import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsApiService } from '../core/analytics.api';

function addMonth(fromISO: string): string {
  const d = new Date(fromISO + 'T00:00:00');
  const y = d.getUTCFullYear();
  const m = d.getUTCMonth();
  const next = new Date(Date.UTC(y, m + 1, 1));
  return next.toISOString().slice(0, 10);
}

@Component({
  selector: 'app-month-select',
  standalone: true,
  imports: [CommonModule],
  template: `
  <label>
    <span>Mes:</span>
    <select #monthSelect [value]="selected()" (change)="onChange(monthSelect.value)">
      <option *ngFor="let m of months()" [value]="m">{{ m }}</option>
    </select>
  </label>
  `,
  styles: [`label{display:inline-flex;gap:.5rem;align-items:center} select{padding:.25rem .5rem}`]
})
export class MonthSelectComponent {
  private api = inject(AnalyticsApiService);

  months = signal<string[]>([]);
  selected = signal<string>('');

  @Output() rangeChange = new EventEmitter<{ from: string; to: string }>();

  ngOnInit(): void {
    this.api.months().subscribe({
      next: months => {
        this.months.set(months);
        const first = months?.[0];
        if (first) {
          this.selected.set(first);
          this.emit(first);
        }
      }
    });
  }

  onChange(value: string): void {
    this.selected.set(value);
    this.emit(value);
  }

  private emit(from: string): void {
    this.rangeChange.emit({ from, to: addMonth(from) });
  }
}
