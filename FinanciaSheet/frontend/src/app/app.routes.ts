import { Routes } from '@angular/router';
import { canActivateAuth } from './core/auth-guard';

export const appRoutes: Routes = [
  { path: 'login',    loadComponent: () => import('./auth/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./auth/register').then(m => m.RegisterComponent) },

  // protegidas
  { path: 'dashboard', canActivate: [canActivateAuth],
    loadComponent: () => import('./dashboard/home').then(m => m.HomeComponent) },
  { path: 'transactions', canActivate: [canActivateAuth],
    children: [
      { path: '',       loadComponent: () => import('./transactions/list').then(m => m.ListComponent) },
      { path: 'import', loadComponent: () => import('./transactions/import').then(m => m.ImportComponent) },
    ]},
  { path: 'invoices', canActivate: [canActivateAuth],
    children: [
      { path: '',      loadComponent: () => import('./invoices/list').then(m => m.List) },
      { path: 'upload',loadComponent: () => import('./invoices/upload').then(m => m.Upload) },
    ]},
  { path: 'settings', canActivate: [canActivateAuth],
    children: [
      { path: '', loadComponent: () => import('./settings/profile').then(m => m.Profile) },
      { path: 'accounts', loadComponent: () => import('./settings/accounts').then(m => m.Accounts) },
    ]},

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
