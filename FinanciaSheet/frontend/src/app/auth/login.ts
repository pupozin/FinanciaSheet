import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <h2>Login</h2>
    <form [formGroup]="form" (ngSubmit)="submit()">
      <input type="email" placeholder="Email" formControlName="email" />
      <input type="password" placeholder="Senha" formControlName="password" />
      <button type="submit" [disabled]="form.invalid || loading">Entrar</button>
    </form>
    <p *ngIf="error" style="color:#c00">{{error}}</p>
    <a routerLink="/register">Criar conta</a>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  loading=false; error:string|null=null;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  submit(){
    if (this.form.invalid) return;
    this.loading = true; this.error=null;
    this.auth.login(this.form.value as any).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: () => { this.error = 'Falha no login'; this.loading=false; }
    });
  }
}
