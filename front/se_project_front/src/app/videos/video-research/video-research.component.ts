import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { VideoService, Content, Category } from '../../services/video.service';
import { forkJoin, catchError, of, map } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

interface EnhancedContent extends Content {
  categoryNames?: string[];
  rating?: number;
}

@Component({
  selector: 'app-video-research',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './video-research.component.html',
  styleUrls: ['./video-research.component.css']
})
export class VideoResearchComponent implements OnInit {
  constructor(private router: Router, private videoService: VideoService, private auth: AuthService) {}

  searchText = '';
  selectedCategory = '';
  selectedDuration = '';
  selectedRating = '';
  selectedType = '';
  selectedLanguage = '';
  selectedDifficulty: number | null = null;

  allVideos: EnhancedContent[] = [];
  categories: Category[] = [];
  languages: string[] = [];
  types: string[] = ['video', 'audio'];

  currentPage = 1;
  pageSize = 12;

  loading = true;

  ngOnInit() {
    this.auth.checkAccess();

    this.loadAllData();
  }

  loadAllData() {
    this.loading = true;
    
    forkJoin({
      videos: this.videoService.getContentByType('video').pipe(catchError(() => of([]))),
      audios: this.videoService.getContentByType('audio').pipe(catchError(() => of([]))),
      categories: this.videoService.getCategories().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ videos, audios, categories }) => {
        const combined: EnhancedContent[] = [...videos, ...audios];
        this.categories = categories;

        const detailRequests = combined.map(content => 
          forkJoin({
            catDetails: this.videoService.getContentCategories(content.id!).pipe(catchError(() => of([]))),
            rating: this.videoService.getMeanRating(content.id!).pipe(catchError(() => of(-1)))
          }).pipe(
            map(details => {
              content.categoryNames = details.catDetails.map(c => c.name);
              const finalRating = details.rating != -1 ? details.rating / 2 : -1;
              content.rating = finalRating;
              return content;
            })
          )
        );

        if (detailRequests.length > 0) {
          forkJoin(detailRequests).subscribe(enrichedVideos => {
            this.allVideos = enrichedVideos;
            this.finalizeLoading();
          });
        } else {
          this.allVideos = [];
          this.finalizeLoading();
        }
      },
      error: () => this.loading = false
    });
  }

  finalizeLoading() {
    this.languages = Array.from(new Set(this.allVideos.map(v => v.language)));
    this.loading = false;
  }

  getFilteredResults(): EnhancedContent[] {
    return this.allVideos.filter(v => {
      if (this.searchText && !v.title.toLowerCase().includes(this.searchText.toLowerCase())) return false;
      if (this.selectedType && v.type !== this.selectedType.toLowerCase()) return false;
      if (this.selectedLanguage && v.language !== this.selectedLanguage) return false;
      if (this.selectedDifficulty !== null && v.difficulty !== this.selectedDifficulty) return false;

      if (this.selectedCategory) {
        const categoryObj = this.categories.find(c => c.id === this.selectedCategory);
        if (!v.categoryNames?.includes(categoryObj?.name || '')) return false;
      }

      if (this.selectedRating) {
        const currentRating = v.rating ?? -1;

        if (this.selectedRating === 'none') {
          if (currentRating !== -1) return false;
        } else {
          const minRating = parseFloat(this.selectedRating);
          if (currentRating === -1 || currentRating < minRating) return false;
        }
      }

      if (this.selectedDuration === 'short' && v.durationMin >= 10) return false;
      if (this.selectedDuration === 'medium' && (v.durationMin < 10 || v.durationMin > 20)) return false;
      if (this.selectedDuration === 'long' && v.durationMin <= 20) return false;

      return true;
    });
  }

  paginatedVideos() {
    const filtered = this.getFilteredResults();
    const start = (this.currentPage - 1) * this.pageSize;
    return filtered.slice(start, start + this.pageSize);
  }

  totalPages(): number {
    const count = this.getFilteredResults().length;
    return Math.ceil(count / this.pageSize) || 1;
  }

  onPageClick(p: number | string) {
    if (typeof p === 'number') this.currentPage = p;
  }

  nextPage() {
    if (this.currentPage < this.totalPages()) this.currentPage++;
  }

  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }

  pagesToDisplay(): (number | string)[] {
    const total = this.totalPages();
    const current = this.currentPage;
    const pages: (number | string)[] = [];
    if (total <= 1) return [1];

    pages.push(1);
    if (current > 3) pages.push('...');
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) {
      pages.push(i);
    }
    if (current < total - 2) pages.push('...');
    pages.push(total);
    return pages;
  }

  onFilterChange() {
    this.currentPage = 1;
  }

  openVideo(video: Content) {
    this.router.navigate(['/videos/detail', video.id], { state: { video } });
  }

  back() { 
    this.router.navigate(['home']); 
  }
}
