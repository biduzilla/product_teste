import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { environment } from "../../environments/environment";
import { Observable } from "rxjs";
import { PageResponse } from "../models/page-response.model";
import { ProductRequest } from "../models/product-request.model";
import { ProductStatus, Product } from "../models/product.model";

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/produtos`;

  list(params: {
    category?: string;
    status?: ProductStatus;
    name?: string;
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<PageResponse<Product>> {
    let httpParams = new HttpParams();

    if (params.category) httpParams = httpParams.set('category', params.category);
    if (params.status) httpParams = httpParams.set('status', params.status);
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    if (params.sort) httpParams = httpParams.set('sort', params.sort);

    return this.http.get<PageResponse<Product>>(this.baseUrl, { params: httpParams });
  }

  findById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  create(req: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, req);
  }

  update(id: string, req: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
