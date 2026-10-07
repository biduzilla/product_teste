import { ProductStatus } from './product.model';

export interface ProductRequest {
  name: string;
  description: string | null;
  price: number;
  category: string;
  status: ProductStatus;
}
