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

  onFile(f: File | null){ this.file.set(f); this.result.set(null); this.error.set(null); }

  upload(kind: 'EXTRATO'|'FATURA'){
    const f = this.file();
    if (!f){ this.error.set('Selecione um arquivo .csv'); return; }
    this.loading.set(true); this.error.set(null); this.result.set(null);
    const source = kind === 'EXTRATO' ? 'nubank_csv' : 'xp_csv'; // ajuste como quiser
    const account = kind === 'EXTRATO' ? 'nubank' : 'xp';

    this.api.uploadCsv(kind, f, source, account).subscribe({
      next: r => { this.result.set(r); this.loading.set(false); },
      error: e => { this.error.set(e?.error?.message || 'Falha no upload'); this.loading.set(false); }
    });
  }
}
