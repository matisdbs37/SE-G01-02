import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Single comment entry linked to a history record
export interface CommentsEntry {
  text: string;
  postedAt: string;
}

// History entry representing a user's interaction with a content
export interface HistoryEntry {
  id?: string;
  userId: string;
  contentId: string;
  watchedAt: string;
  contentDuration: number;
  watchedDuration: number;
  rating?: number;
  comments: CommentsEntry[];
  createdAt?: string;
  updatedAt?: string;
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
export class HistoryService {
  // Inject HttpClient
  private http = inject(HttpClient);

  // Base API endpoint for history-related operations
  private readonly API_URL = environment.apiUrl + '/api/v2/history';

  // Retrieve the user's viewing history with pagination
  getHistory(page: number = 0, size: number = 20): Observable<Page<HistoryEntry>> {
    const params = new HttpParams()
      .set('pages', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<HistoryEntry>>(this.API_URL, { params });
  }

  // Update watched duration for a specific video and for the current user
  updateProgress(videoId: string, watchTime: number): Observable<void> {
    const params = new HttpParams().set('watchTime', watchTime.toString());
    return this.http.patch<void>(`${this.API_URL}/${videoId}/progress`, {}, { params });
  }

  // Rate a video using a star-based system for the current user
  rateVideo(videoId: string, stars: number): Observable<HistoryEntry> {
    const params = new HttpParams().set('stars', stars.toString());
    return this.http.patch<HistoryEntry>(`${this.API_URL}/${videoId}/rating`, {}, { params });
  }

  // Add a comment to a watched video for the current user
  addComment(videoId: string, text: string): Observable<HistoryEntry> {
    const params = new HttpParams().set('text', text);
    return this.http.put<HistoryEntry>(`${this.API_URL}/${videoId}/comment`, {}, { params });
  }
}