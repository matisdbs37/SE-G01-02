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
      categories: this.videoService.getCategories().pipe(catchError(() => of([]))),
      allLinks: this.videoService.getAllCategoryAssociations().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ videos, audios, categories, allLinks }) => {
        const combined = [...videos, ...audios] as EnhancedContent[];
        this.categories = categories;

        const enrichedWithCats = combined.map(content => {
          const linkIds = allLinks.filter(l => l.contentId === content.id).map(l => l.categoryId);
          content.categoryNames = categories.filter(c => linkIds.includes(c.id)).map(c => c.name);
          content.rating = -1;
          return content;
        });

        const ratingRequests = enrichedWithCats.map(v => 
          this.videoService.getMeanRating(v.id!).pipe(
            map(r => ({ id: v.id, rating: r })),
            catchError(() => of({ id: v.id, rating: -1 }))
          )
        );

        if (ratingRequests.length > 0) {
          forkJoin(ratingRequests).subscribe(ratings => {
            ratings.forEach(res => {
              const video = enrichedWithCats.find(v => v.id === res.id);
              if (video) {
                video.rating = res.rating !== -1 ? res.rating / 2 : -1;
              }
            });
            this.allVideos = enrichedWithCats;
            this.finalizeLoading();
          });
        } else {
          this.allVideos = enrichedWithCats;
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

  getThumbnail(url: string): string | null {
    if (!url) return null;

    if (url.includes('youtube.com') || url.includes('youtu.be')) {
      const videoId = url.split('v=')[1]?.split('&')[0] || url.split('/').pop();
      return `https://img.youtube.com/vi/${videoId}/mqdefault.jpg`;
    }

    if (url.includes('vimeo.com')) {
      const videoId = url.split('/').pop();
      return `https://vumbnail.com/${videoId}.jpg`;
    }

    return null; 
  }

  back() { 
    this.router.navigate(['home']); 
  }
}
