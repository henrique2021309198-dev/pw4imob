import { Routes } from '@angular/router';
import { adminGuard, customerGuard, guestGuard } from './auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./property-list').then((module) => module.PropertyList),
  },
  {
    path: 'imoveis/:id',
    loadComponent: () => import('./property-detail').then((module) => module.PropertyDetail),
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./login').then((module) => module.Login),
  },
  {
    path: 'cliente',
    canActivate: [customerGuard],
    loadComponent: () => import('./customer-area').then((module) => module.CustomerArea),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./admin-panel').then((module) => module.AdminPanel),
  },
  { path: '**', redirectTo: '' },
];
