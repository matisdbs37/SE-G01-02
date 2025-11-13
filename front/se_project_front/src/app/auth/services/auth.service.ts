import { Injectable } from '@angular/core';
import { Observable, of, throwError, delay } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  login(email: string, password: string, remember: boolean): Observable<{token:string}> {
    if (!email || !password) return throwError(() => new Error('Missing credentials'));
    // TODO: call backend here
    return of({ token: 'fake-jwt-token' }).pipe(delay(600));
  }

  register(email: string, password: string): Observable<{ok:boolean}> {
    // TODO: call backend here
    return of({ ok: true }).pipe(delay(600));
  }

  oauth(provider: 'google'|'apple'|'microsoft') {
    // TODO: redirection OAuth
    console.log('OAuth ->', provider);
  }
}
