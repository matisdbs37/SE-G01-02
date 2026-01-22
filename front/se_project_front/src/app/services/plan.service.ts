import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Difficulty levels for a meditation plan
export enum PlanLevel {
  EASY = 'EASY',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

// Single entry inside a plan (content to watch)
export interface PlanEntry {
  id?: string;
  content: string;
  notified: boolean;
  contentId: string;
}

// Meditation plan linked to a user
export interface Plan {
  id?: string;
  userId: string;
  level: PlanLevel;
  toWatch: PlanEntry[];
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PlanService {
  // Inject HttpClient
  private http = inject(HttpClient);

  // Base API endpoint for plan-related operations
  private readonly API_URL = `${environment.apiUrl}/api/v2/plan/`;

  // Retrieve all plans of the current user
  getMyPlans(): Observable<Plan[]> {
    return this.http.get<Plan[]>(this.API_URL);
  }

  // Create a new plan based on the selected difficulty level for the current user
  createPlan(level: PlanLevel): Observable<string> {
    return this.http.post(`${this.API_URL}${level}`, {}, { 
      responseType: 'text' 
    });
  }
}