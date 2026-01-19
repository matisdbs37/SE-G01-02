import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Content {
  id?: string;
  title: string;
  type: string;
  durationMin: number;
  difficulty?: number;
  language: string;
  source?: string;
  isActive?: boolean;
  createdAt?: string;
}

export interface Categories {
  id?: string;
  name: string;
}

export interface ContentCategory {
  id?: string;
  contentId: string;
  categoryId: string;
}

@Injectable({
  providedIn: 'root'
})
export class VideoService {
  private http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl + '/api/v2';

  getContentByTitle(title: string): Observable<Content> {
    return this.http.get<Content>(`${this.API_URL}/content/${encodeURIComponent(title)}`);
  }

  getContentByType(type: string): Observable<Content[]> {
    return this.http.get<Content[]>(`${this.API_URL}/content/type/${encodeURIComponent(type)}`);
  }

  getCategories(): Observable<Categories[]> {
    return this.http.get<Categories[]>(`${this.API_URL}/categories/`);
  }
}
