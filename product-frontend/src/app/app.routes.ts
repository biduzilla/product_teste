import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'produtos', pathMatch: 'full' },
  {
    path: 'produtos',
    loadComponent: () =>
      import('./features/products/product-list/product-list.component')
        .then(m => m.ProductListComponent),
  },
  {
    path: 'produtos/novo',
    loadComponent: () =>
      import('./features/products/product-form/product-form.component')
        .then(m => m.ProductFormComponent),
  },
  {
    path: 'produtos/:id/editar',
    loadComponent: () =>
      import('./features/products/product-form/product-form.component')
        .then(m => m.ProductFormComponent),
  },
  { path: '**', redirectTo: 'produtos' },
];
