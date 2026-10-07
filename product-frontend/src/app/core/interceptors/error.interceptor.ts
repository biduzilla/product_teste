import { HttpInterceptorFn, HttpErrorResponse } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, throwError } from "rxjs";
import { ApiError } from "../models/api-error.model";
import { MatSnackBar } from '@angular/material/snack-bar';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const apiError = error.error as ApiError;

      if (apiError?.fields) {
        const messages = Object.entries(apiError.fields)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join('\n');
        snackBar.open(messages, 'Fechar', { duration: 6000 });
      } else if (apiError?.message) {
        snackBar.open(apiError.message, 'Fechar', { duration: 5000 });
      } else {
        snackBar.open('Erro inesperado. Tente novamente.', 'Fechar', { duration: 5000 });
      }

      return throwError(() => error);
    })
  );
};
