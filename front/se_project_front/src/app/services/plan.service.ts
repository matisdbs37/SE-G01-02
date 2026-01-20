import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export enum PlanLevel {
  EASY = 'EASY',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

export interface PlanEntry {
  id?: string;
  content: string;
  notified: boolean;
  contentId: string;
}

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
  private http = inject(HttpClient);

  private readonly API_URL = `${environment.apiUrl}/api/v2/plan/`;

  getMyPlans(): Observable<Plan[]> {
    return this.http.get<Plan[]>(this.API_URL);
  }

  createPlan(level: PlanLevel): Observable<string> {
    return this.http.post(`${this.API_URL}${level}`, {}, { 
      responseType: 'text' 
    });
  }
}