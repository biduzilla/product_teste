import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2 mat-dialog-title class="text-lg font-semibold text-slate-900">
      {{ data.title }}
    </h2>

    <mat-dialog-content class="text-sm text-slate-600">
      {{ data.message }}
    </mat-dialog-content>

    <mat-dialog-actions align="end" class="gap-2 px-4 pb-4">
      <button
        mat-stroked-button
        type="button"
        (click)="onCancel()"
        class="text-slate-700!">
        {{ data.cancelText ?? 'Cancelar' }}
      </button>

      <button
        mat-flat-button
        color="warn"
        type="button"
        (click)="onConfirm()">
        {{ data.confirmText ?? 'Confirmar' }}
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmDialogComponent {
  protected readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<ConfirmDialogComponent>);

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
