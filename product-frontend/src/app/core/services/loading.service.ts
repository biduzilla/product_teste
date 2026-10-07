import { Injectable, signal } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  private counter = 0;

  show(): void {
    this.counter++;
    this._loading.set(true);
  }

  hide(): void {
    this.counter = Math.max(0, this.counter - 1);
    if (this.counter === 0) {
      this._loading.set(false);
    }
  }
}
