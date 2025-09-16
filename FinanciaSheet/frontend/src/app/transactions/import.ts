import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadComponent } from '../shared/file-upload';
import { ApiService, ImportResponse } from '../core/api';

@Component({
  selector: 'app-transactions-import',
  standalone: true,
  imports: [CommonModule, FileUploadComponent],
  templateUrl: './import.html',
  styleUrls: ['./import.scss']
})
export class ImportComponent {
  private api = inject(ApiService);

  file = signal<File | null>(null);
  loading = signal(false);
  result = signal<ImportResponse | null>(null);
  error = signal<string | null>(null);
  suggestedKind = signal<'EXTRATO' | 'FATURA' | null>(null);

  private source = 'unknown_csv';
  private account = '';

  async onFile(f: File | null){
    this.file.set(f);
    this.result.set(null);
    this.error.set(null);
    this.suggestedKind.set(null);
    this.source = 'unknown_csv';
    this.account = '';

    if (!f) {
      return;
    }

    const sanitized = this.sanitizeName(f.name);
    this.source = sanitized;
    this.account = sanitized;

    try {
      const sample = await f.slice(0, 4096).text();
      const detected = this.detectKind(sample);
      if (detected) {
        this.suggestedKind.set(detected);
      }
    } catch {
      // se a leitura falhar, ignoramos e mantemos o estado padrao
    }
  }

  upload(kind: 'EXTRATO' | 'FATURA'){
    const f = this.file();
    if (!f){
      this.error.set('Selecione um arquivo .csv');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.result.set(null);

    const src = (this.source || '').trim() || 'unknown_csv';
    const acc = (this.account || '').trim();

    this.api.uploadCsv(kind, f, src, acc || undefined).subscribe({
      next: r => { this.result.set(r); this.loading.set(false); },
      error: e => {
        this.error.set(e?.error?.message || 'Falha no upload');
        this.loading.set(false);
      }
    });
  }

  private detectKind(sample: string): 'EXTRATO' | 'FATURA' | null {
    const header = sample.split(/\r?\n/, 1)[0]?.toLowerCase() ?? '';
    if (header.includes('dataestabelecimentoportadorvalorparcela')) {
      return 'FATURA';
    }
    if (header.includes('amount') && header.includes('title')) {
      return 'FATURA';
    }
    if (header.includes('descricao') || header.includes('description')) {
      return 'EXTRATO';
    }
    return null;
  }

  private sanitizeName(name: string){
    const base = name.split('.')[0] ?? name;
    const normalized = base
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '_')
      .replace(/^_+|_+$/g, '');
    return normalized || 'unknown_csv';
  }
}
