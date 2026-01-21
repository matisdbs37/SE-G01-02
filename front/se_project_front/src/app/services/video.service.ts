import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Content {
  id?: string;
  url: string;
  title: string;
  type: 'video' | 'audio';
  durationMin: number;
  difficulty?: number;
  language: string;
  source?: string;
  isActive?: boolean;
  createdAt?: string;
}

export interface Category {
  id: string;
  name: string;
}

export interface ContentCategory {
  id?: string;
  contentId: string;
  categoryId: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class VideoService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/api/v2/content`;

  getAllContent(page: number = 0, size: number = 20): Observable<Page<Content>> {
    const params = new HttpParams()
      .set('pages', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Content>>(`${this.API_URL}/all`, { params });
  }

  getContentByTitle(title: string): Observable<Content> {
    return this.http.get<Content>(`${this.API_URL}/${encodeURIComponent(title)}`);
  }

  getContentByType(type: string): Observable<Content[]> {
    return this.http.get<Content[]>(`${this.API_URL}/type/${type.toLowerCase()}`);
  }

  getAllCategoryAssociations(): Observable<ContentCategory[]> {
    return this.http.get<ContentCategory[]>(`${this.API_URL}/categories`);
  }

  getContentCategories(contentId: string): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.API_URL}/categories/${contentId}`);
  }

  assignCategory(association: ContentCategory): Observable<ContentCategory> {
    return this.http.post<ContentCategory>(`${this.API_URL}/categories`, association);
  }

  removeCategoryLink(associationId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/categories/${associationId}`);
  }

  getMeanRating(id: string): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/rating/${id}`);
  }

  getInteractions(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/interactions/${id}`);
  }

  createContent(content: Content): Observable<Content> {
    return this.http.post<Content>(this.API_URL, content);
  }

  updateContent(id: string, content: Content): Observable<Content> {
    return this.http.put<Content>(`${this.API_URL}/${id}`, content);
  }

  deleteContent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${environment.apiUrl}/api/v2/categories/`);
  }
}