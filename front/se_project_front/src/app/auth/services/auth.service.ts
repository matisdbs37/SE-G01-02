import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';

export interface MockUser {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  stressLevel?: number;
  goal?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // 🔒 Storage key (centralisé)
  private readonly STORAGE_KEY = 'mindcraft_user';

  constructor() {}

  // -------------------------
  // LOGIN (signature inchangée)
  // -------------------------
  login(email: String, password: String): Observable<any> {
    const stored = localStorage.getItem(this.STORAGE_KEY);

    if (!stored) {
      return throwError(() => new Error('No user registered'));
    }

    const user: MockUser = JSON.parse(stored);

    if (user.email === email && user.password === password) {
      return of(user);
    }

    return throwError(() => new Error('Invalid credentials'));
  }

  // -------------------------
  // REGISTER (signature inchangée)
  // -------------------------
  register(
    email: String,
    password: String,
    firstName: String = 'John',
    lastName: String = 'DOE'
  ): Observable<any> {

    const user: MockUser = {
      email: email.toString(),
      password: password.toString(),
      firstName: firstName.toString(),
      lastName: lastName.toString()
    };

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
    return of(user);
  }

  // -------------------------
  // LOGOUT (signature inchangée)
  // -------------------------
  logout(): Observable<any> {
    localStorage.removeItem(this.STORAGE_KEY);
    return of({ message: 'Logged out' });
  }

  // -------------------------
  // OAUTH (stub – compatibility)
  // -------------------------
  oauth(): void {
    console.warn('[AuthService] OAuth disabled in mock mode');
  }

  // -------------------------
  // GET USER (signature inchangée)
  // -------------------------
  getUser(): Observable<any> {
    const user = localStorage.getItem(this.STORAGE_KEY);
    return of(user ? JSON.parse(user) : null);
  }

  // -------------------------
  // UPDATE USER (needed by profile)
  // -------------------------
  updateUser(email: string, firstName: string, lastName: string): Observable<any> {
    const stored = localStorage.getItem(this.STORAGE_KEY);

    if (!stored) {
      return of(null);
    }

    const user: MockUser = JSON.parse(stored);

    if (user.email !== email) {
      return of(null);
    }

    const updatedUser: MockUser = {
      ...user,
      firstName,
      lastName
    };

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(updatedUser));
    return of(updatedUser);
  }

  // -------------------------
  // Helper (optional)
  // -------------------------
  isLoggedIn(): boolean {
    return !!localStorage.getItem(this.STORAGE_KEY);
  }
}
