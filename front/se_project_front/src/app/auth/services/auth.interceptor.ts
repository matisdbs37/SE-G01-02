import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const oauthService = inject(OAuthService);
  const token = oauthService.getIdToken(); 
  console.log("INTERCEPTOR - URL:", req.url, "TOKEN PRESENT ?:", !!token);
  console.log("token :", token);
  // On n'ajoute le token QUE pour notre API
  if (token && req.url.includes('localhost:8080')) {
    console.log("INTERCEPTOR - Ajout du token pour le backend");
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  return next(req);
};