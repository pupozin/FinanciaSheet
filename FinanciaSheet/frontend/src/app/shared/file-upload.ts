import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.html',
  styleUrls: ['./file-upload.scss']
})
export class FileUploadComponent {
  @Output() fileChange = new EventEmitter<File | null>();
  dragging = signal(false);
  fileName = signal<string>('');

  onDrop(ev: DragEvent){
    ev.preventDefault();
    this.dragging.set(false);
    const f = ev.dataTransfer?.files?.[0] ?? null;
    this.setFile(f);
  }
  onDragOver(ev: DragEvent){ ev.preventDefault(); this.dragging.set(true); }
  onDragLeave(){ this.dragging.set(false); }

  onSelect(ev: Event){
    const input = ev.target as HTMLInputElement;
    const f = input.files?.[0] ?? null;
    this.setFile(f);
  }

  private setFile(f: File | null){
    this.fileName.set(f?.name ?? '');
    this.fileChange.emit(f);
  }

  clear(){ this.setFile(null); }
}
