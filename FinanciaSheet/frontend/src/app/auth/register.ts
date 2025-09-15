// src/app/auth/register.component.ts
import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <h2>Registrar</h2>
    <form [formGroup]="form" (ngSubmit)="submit()">
      <input type="text" placeholder="Nome" formControlName="name" />
      <input type="email" placeholder="Email" formControlName="email" />
      <input type="password" placeholder="Senha (mín. 6)" formControlName="password" />
      <button type="submit" [disabled]="form.invalid || loading">Criar conta</button>
    </form>
    <p *ngIf="error" style="color:#c00">{{error}}</p>
    <a routerLink="/login">Já tenho conta</a>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  loading=false; error:string|null=null;

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  submit(){
    if (this.form.invalid) return;
    this.loading = true; this.error=null;
    this.auth.register(this.form.value as any).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: () => { this.error = 'Falha no registro'; this.loading=false; }
    });
  }
}
