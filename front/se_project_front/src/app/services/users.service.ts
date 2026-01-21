import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

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
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v2/user';

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(this.API_URL);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/infos/${id}`);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/api/v2/users`);
  }

  createUser(userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/create`, userData);
  }

  updateUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/update`, userData);
  }

  deleteUser() {
    return this.http.delete(`${this.API_URL}/delete`, { responseType: 'text' });
  }

  logUserActivity(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/log`);
  }

  getUserRole(): Observable<string> {
    return this.http.get<string>(`${this.API_URL}/role`);
  }

  triggerEmail(): Observable<string> {
    return this.http.get(environment.apiUrl + '/api/v2/mail/trigger', { responseType: 'text' });
  }
}