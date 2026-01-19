import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CommentsEntry {
  text: string;
  postedAt: string;
}

export interface HistoryEntry {
  id?: string;
  userId: string;
  videoId: string;
  watchedAt: string;
  contentDuration: number;
  watchedDuration: number;
  rating?: number;
  comments: CommentsEntry[];
  createdAt?: string;
  updatedAt?: string;
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
export class HistoryService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl + '/api/v2/history';

  getHistory(page: number = 0, size: number = 20): Observable<Page<HistoryEntry>> {
    const params = new HttpParams()
      .set('pages', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<HistoryEntry>>(this.API_URL, { params });
  }

  updateProgress(videoId: string, watchTime: number): Observable<void> {
    const params = new HttpParams().set('watchTime', watchTime.toString());
    return this.http.patch<void>(`${this.API_URL}/${videoId}/progress`, {}, { params });
  }

  rateVideo(videoId: string, stars: number): Observable<HistoryEntry> {
    const params = new HttpParams().set('stars', stars.toString());
    return this.http.patch<HistoryEntry>(`${this.API_URL}/${videoId}/rating`, {}, { params });
  }

  addComment(videoId: string, text: string): Observable<HistoryEntry> {
    const params = new HttpParams().set('text', text);
    return this.http.put<HistoryEntry>(`${this.API_URL}/${videoId}/comment`, {}, { params });
  }
}