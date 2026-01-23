import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';

// HTTP interceptor to add Authorization header with Bearer token
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const oauthService = inject(OAuthService);

  // Retrieve the ID token from the OAuth service
  const token = oauthService.getIdToken(); 

  // If a token exists and the request is to the API URL, clone the request to add the Authorization header
  if (token && req.url.includes(environment.apiUrl)) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  return next(req);
};