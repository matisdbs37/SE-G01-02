import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Content model representing a meditation video or audio
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

// Category model used to classify content
export interface Category {
  id: string;
  name: string;
}

// Association between content and category
export interface ContentCategory {
  id?: string;
  contentId: string;
  categoryId: string;
}

// Generic paginated response model
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
  // Inject HttpClient using the standalone API
  private http = inject(HttpClient);

  // Base API endpoint for content-related operations
  private readonly API_URL = `${environment.apiUrl}/api/v2/content`;

  // Retrieve all content with pagination
  getAllContent(page: number = 0, size: number = 20): Observable<Page<Content>> {
    const params = new HttpParams()
      .set('pages', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Content>>(`${this.API_URL}/all`, { params });
  }

  // Retrieve a content item by its title
  getContentByTitle(title: string): Observable<Content> {
    return this.http.get<Content>(`${this.API_URL}/${encodeURIComponent(title)}`);
  }

  // Retrieve content filtered by type (video or audio)
  getContentByType(type: string): Observable<Content[]> {
    return this.http.get<Content[]>(`${this.API_URL}/type/${type.toLowerCase()}`);
  }

  // Retrieve all content-category associations
  getAllCategoryAssociations(): Observable<ContentCategory[]> {
    return this.http.get<ContentCategory[]>(`${this.API_URL}/categories`);
  }

  // Retrieve categories linked to a specific content
  getContentCategories(contentId: string): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.API_URL}/categories/${contentId}`);
  }

  // Assign a category to a content
  assignCategory(association: ContentCategory): Observable<ContentCategory> {
    return this.http.post<ContentCategory>(`${this.API_URL}/categories`, association);
  }

  // Remove a category-content association
  removeCategoryLink(associationId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/categories/${associationId}`);
  }

  // Retrieve the mean rating of a content
  getMeanRating(id: string): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/rating/${id}`);
  }

  // Retrieve user interactions related to a content (rate, comments)
  getInteractions(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/interactions/${id}`);
  }

  // Create a new content
  createContent(content: Content): Observable<Content> {
    return this.http.post<Content>(this.API_URL, content);
  }

  // Update an existing content by its ID
  updateContent(id: string, content: Content): Observable<Content> {
    return this.http.put<Content>(`${this.API_URL}/${id}`, content);
  }

  // Delete a content by its ID
  deleteContent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  // Retrieve all available categories
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${environment.apiUrl}/api/v2/categories/`);
  }
}