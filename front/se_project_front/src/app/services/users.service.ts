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
  // Injection du HttpClient (version moderne Angular)
  private http = inject(HttpClient);

  // L'URL de base définie dans tes fichiers environment
  private readonly API_URL = `http://localhost:8080/api/v2/user`;

  /**
   * 1. Vérifie si l'utilisateur existe
   * Ton collègue a dit : renvoie 404 si l'email n'existe pas
   */
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(this.API_URL);
  }

  /**
   * 2. Crée l'utilisateur (Appelé après le questionnaire)
   * Utilise @PutMapping("user/create")
   * On peut envoyer les infos récoltées dans le questionnaire ici
   */
  createUser(userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/create`, userData);
  }

  /**
   * 3. Met à jour l'utilisateur
   * Utilise @PostMapping("user/update")
   */
  updateUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/update`, userData);
  }
}