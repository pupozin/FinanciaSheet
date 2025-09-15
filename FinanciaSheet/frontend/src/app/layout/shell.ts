import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar';
import { Footer } from './footer';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, Footer],
  template: `
    <app-navbar></app-navbar>
    <main class="container"><router-outlet></router-outlet></main>
    <app-footer></app-footer>
  `,
  styles: [`.container{padding:16px; max-width:1200px; margin:0 auto;}`]
})
export class ShellComponent {}
