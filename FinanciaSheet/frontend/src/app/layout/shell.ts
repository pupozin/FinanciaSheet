import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar';
import { Footer } from './footer';
import { AuthService } from '../core/auth';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, Footer, NgIf],
  template: `
    <ng-container *ngIf="isAuthenticated(); else anonymous">
      <app-navbar></app-navbar>
      <main class="container"><router-outlet></router-outlet></main>
      <app-footer></app-footer>
    </ng-container>
    <ng-template #anonymous>
      <main class="auth-container"><router-outlet></router-outlet></main>
    </ng-template>
  `,
  styles: [
    `.container{padding:16px; max-width:1200px; margin:0 auto;}`,
    `.auth-container{min-height:100vh; display:block;}`
  ]
})
export class ShellComponent {
  private auth = inject(AuthService);

  isAuthenticated(){
    return this.auth.isLoggedIn();
  }
}
