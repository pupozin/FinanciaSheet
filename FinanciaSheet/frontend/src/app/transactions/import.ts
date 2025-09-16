import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FileUploadComponent } from '../shared/file-upload';
import { ApiService, ImportResponse } from '../core/api';

@Component({
  selector: 'app-transactions-import',
  standalone: true,
  imports: [CommonModule, FormsModule, FileUploadComponent],
  templateUrl: './import.html',
  styleUrls: ['./import.scss']
})
export class ImportComponent {
  private api = inject(ApiService);

  file = signal<File | null>(null);
  loading = signal(false);
  result = signal<ImportResponse | null>(null);
  error = signal<string | null>(null);

  kind: 'EXTRATO' | 'FATURA' = 'EXTRATO';
  source = 'unknown_csv';
  account = '';

  async onFile(f: File | null){
    this.file.set(f);
    this.result.set(null);
    this.error.set(null);

    if (!f) { return; }
    if (this.source === 'unknown_csv') {
      this.source = this.sanitizeName(f.name);
    }
    if (!this.account) {
      this.account = this.sanitizeName(f.name);
    }

    try {
      const sample = await f.slice(0, 4096).text();
      this.detectKind(sample);
    } catch {
      // se a leitura falhar, ignoramos e mantemos a escolha atual
    }
  }

  upload(){
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

    this.api.uploadCsv(this.kind, f, src, acc || undefined).subscribe({
      next: r => { this.result.set(r); this.loading.set(false); },
      error: e => {
        this.error.set(e?.error?.message || 'Falha no upload');
        this.loading.set(false);
      }
    });
  }

  private detectKind(sample: string){
    const header = sample.split(/\r?\n/, 1)[0]?.toLowerCase() ?? '';
    if (header.includes('dataestabelecimentoportadorvalorparcela')) {
      this.kind = 'FATURA';
    } else if (header.includes('amount') && header.includes('title')) {
      this.kind = 'FATURA';
    } else if (header.includes('descricao') || header.includes('description')) {
      this.kind = 'EXTRATO';
    }
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
