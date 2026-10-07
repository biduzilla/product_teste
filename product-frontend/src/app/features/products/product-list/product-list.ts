import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Product } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { CurrencyPipe } from '@angular/common';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  imports: [
    CurrencyPipe,
    MatPaginatorModule,
    MatDialogModule,
    MatSnackBarModule,
  ],
  selector: 'app-product-list',
  styleUrl: './product-list.css',
  templateUrl: './product-list.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductListComponent {
  private readonly productService = inject(ProductService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly page = signal(0);
  readonly size = signal(20);
  readonly totalElements = signal(0);
  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly isEmpty = computed(() => !this.loading() && this.products().length === 0);

  constructor() {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService.list({
      page: this.page(),
      size: this.size(),
    }).subscribe({
      next: res => {
        this.products.set(res.content);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onPageChange(event: PageEvent): void {
    this.page.set(event.pageIndex);
    this.size.set(event.pageSize);
    this.loadProducts();
  }

  onNew(): void {
    this.router.navigate(['/produtos/novo']);
  }

  onEdit(id: string): void {
    this.router.navigate(['/produtos', id, 'editar']);
  }

  onDelete(product: Product): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: { title: 'Remover produto', message: `Deseja remover "${product.name}"?` },
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (!confirmed) return;
      this.productService.delete(product.id).subscribe(() => {
        this.snackBar.open('Produto removido com sucesso', 'Fechar', { duration: 3000 });
        this.loadProducts();
      });
    });
  }
}
