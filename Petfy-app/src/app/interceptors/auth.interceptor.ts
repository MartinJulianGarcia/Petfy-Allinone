import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const currentUser = authService.getCurrentUser();

  // Si hay un usuario logueado y la petición es al backend, agregar header de Authorization
  if (currentUser && req.url.startsWith('http://localhost:8080/api/')) {
    // Solo agregar Authorization si no está ya presente
    if (!req.headers.has('Authorization')) {
      const credentials = btoa(`${currentUser.email}:${currentUser.password}`);
      const clonedReq = req.clone({
        setHeaders: {
          Authorization: `Basic ${credentials}`
        }
      });
      return next(clonedReq);
    }
  }

  return next(req);
};

