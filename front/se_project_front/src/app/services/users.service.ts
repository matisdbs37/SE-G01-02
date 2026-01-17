import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface pour typer l'utilisateur (à adapter selon les champs de ton back)
export interface User {
  id?: string;            // UUID ou ID string du back
  email: string;
  firstName: string;
  lastName: string;
  roles?: string;
  city?: string;
  locale?: string;
  preferences?: string;
  isActive: boolean;
  
  // Champs de l'app de mindfulness (0 à 10 selon ton JSON)
  mental: number;
  sleep: number;
  stress: number;
  meditation: number;

  // Dates au format ISO
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);

  private readonly API_URL = `http://localhost:8080/api/v2/user`;

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(this.API_URL);
  }

  createUser(userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/create`, userData);
  }

  updateUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/update`, userData);
  }
}