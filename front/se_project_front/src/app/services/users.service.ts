import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// User model representing the backend user entity
export interface User {
  id?: string;
  email: string;
  firstName: string;
  lastName: string;
  roles?: string;
  city?: string;
  locale?: string;
  preferences?: string;
  isActive: boolean;

  // User well-being metrics
  mental: number;
  sleep: number;
  stress: number;
  meditation: number;

  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  // Inject HttpClient using Angular's inject() function
  private http = inject(HttpClient);

  // Base API endpoint for user-related operations
  private readonly API_URL = environment.apiUrl + '/api/v2/user';

  // Retrieve the currently authenticated user
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(this.API_URL);
  }

  // Retrieve a user by their unique identifier
  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/infos/${id}`);
  }

  // Retrieve all users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/api/v2/users`);
  }

  // Create a new user
  createUser(userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/create`, userData);
  }

  // Update the currently authenticated user
  updateUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/update`, userData);
  }

  // Delete the currently authenticated user
  deleteUser() {
    return this.http.delete(`${this.API_URL}/delete`, { responseType: 'text' });
  }

  // Log a user activity (e.g. login or app usage)
  logUserActivity(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/log`);
  }

  // Retrieve the role of the current user
  getUserRole(): Observable<string> {
    return this.http.get<string>(`${this.API_URL}/role`);
  }

  // Trigger an email (e.g. notification or reminder)
  triggerEmail(): Observable<string> {
    return this.http.get(
      environment.apiUrl + '/api/v2/mail/trigger',
      { responseType: 'text' }
    );
  }
}