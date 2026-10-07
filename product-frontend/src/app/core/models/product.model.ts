export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED';

export interface Product {
  id: string;
  name: string;
  description: string | null;
  price: number;
  category: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string;
}
