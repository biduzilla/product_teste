import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ProductStatus } from '../../../core/models/product.model';
import { FormField, form, required, minLength, maxLength, min } from '@angular/forms/signals';
import { Router, ActivatedRoute } from '@angular/router';
import { ProductRequest } from '../../../core/models/product-request.model';
import { ProductService } from '../../../core/services/product.service';

interface ProductFormData {
  name: string;
  description: string;
  price: number | null;
  category: string;
  status: ProductStatus;
}

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [FormField],
  templateUrl: './product-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductFormComponent {
  private readonly productService = inject(ProductService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly productId = signal<string | null>(null);
  readonly isEdit = computed(() => this.productId() !== null);
  readonly isPending = signal(false);

  readonly statuses: ProductStatus[] = ['ACTIVE', 'INACTIVE', 'DISCONTINUED'];

  readonly model = signal<ProductFormData>({
    name: '',
    description: '',
    price: null,
    category: '',
    status: 'ACTIVE',
  });

  readonly productForm = form(this.model, (path) => {
    required(path.name, { message: 'Nome é obrigatório' });
    minLength(path.name, 3, { message: 'Nome deve ter pelo menos 3 caracteres' });
    maxLength(path.name, 120, { message: 'Nome deve ter no máximo 120 caracteres' });

    maxLength(path.description, 500, { message: 'Descrição deve ter no máximo 500 caracteres' });

    required(path.price, { message: 'Preço é obrigatório' });
    min(path.price, 0.01, { message: 'Preço deve ser maior que zero' });

    required(path.category, { message: 'Categoria é obrigatória' });
    maxLength(path.category, 40, { message: 'Categoria deve ter no máximo 40 caracteres' });

    required(path.status, { message: 'Status é obrigatório' });
  });

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productId.set(id);
      this.loadProduct(id);
    }
  }

  private loadProduct(id: string): void {
    this.productService.findById(id).subscribe({
      next: (product) => {
        this.model.set({
          name: product.name,
          description: product.description ?? '',
          price: product.price,
          category: product.category,
          status: product.status,
        });
      },
    });
  }

  onSubmit(): void {
    if (this.productForm().invalid()) return;

    this.isPending.set(true);

    const data = this.model();
    const request: ProductRequest = {
      name: data.name,
      description: data.description || null,
      price: data.price!,
      category: data.category,
      status: data.status,
    };

    const id = this.productId();
    const action$ = id
      ? this.productService.update(id, request)
      : this.productService.create(request);

    action$.subscribe({
      next: () => {
        this.router.navigate(['/produtos']);
      },
      complete: () => this.isPending.set(false),
    });
  }

  onCancel(): void {
    this.router.navigate(['/produtos']);
  }
}
