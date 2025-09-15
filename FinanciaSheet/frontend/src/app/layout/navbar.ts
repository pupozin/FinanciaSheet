import { Component, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <header class="nav">
      <a routerLink="/dashboard">FinanciaSheet</a>
      <nav>
        <a routerLink="/transactions">Transações</a>
        <a routerLink="/invoices">Faturas</a>
        <a routerLink="/settings">Config</a>
        <button *ngIf="auth.isLoggedIn()" (click)="logout()">Sair</button>
      </nav>
    </header>
  `,
  styles: [`.nav{display:flex;justify-content:space-between;gap:16px;align-items:center;padding:12px 16px;border-bottom:1px solid #eee}`]
})
export class NavbarComponent {
  auth = inject(AuthService);
  private router = inject(Router);
  logout(){ this.auth.logout(); this.router.navigateByUrl('/login'); }
}
